<template>
  <PaperTexture variant="light" class="min-h-screen">
    <div class="post-new">
      <HandCard variant="paper" class="post-new__card">
        <Pin variant="blue" position="top" />

        <header class="post-new__header">
          <h1 class="post-new__title">{{ isEdit ? '编辑时刻' : '记录新时刻' }}</h1>
          <div class="post-new__actions">
            <HandButton variant="ghost" size="sm" @click="goBack">
              取消
            </HandButton>
            <HandButton variant="primary" size="sm" @click="publish" :disabled="saving">
              {{ saving ? '保存中...' : '发布' }}
            </HandButton>
          </div>
        </header>

        <!-- 加载状态 -->
        <HandLoading v-if="loading" text="正在加载..." />

        <!-- 编辑表单 -->
        <form v-else class="post-new__form" @submit.prevent="publish">
          <!-- 标题 -->
          <div class="form-section">
            <label for="title">标题</label>
            <HandInput
              id="title"
              v-model="form.title"
              placeholder="给这个时刻起个名字..."
              class="title-input"
            />
          </div>

          <!-- 富文本编辑器 -->
          <div class="form-section">
            <label>内容</label>
            <div class="rich-editor">
              <!-- 编辑器工具栏 -->
              <div class="editor-toolbar">
                <div class="toolbar-group">
                  <HandButton
                    type="button"
                    variant="ghost"
                    size="sm"
                    title="添加图片"
                    @click="triggerImageUpload"
                  >
                    📷
                  </HandButton>
                  <HandButton
                    type="button"
                    variant="ghost"
                    size="sm"
                    title="段落换行"
                    @click="insertParagraph"
                  >
                    ↵
                  </HandButton>
                </div>
              </div>

              <!-- 编辑区域 -->
              <div
                ref="editorContent"
                class="editor-content"
                contenteditable="true"
                @input="handleContentChange"
                @paste="handlePaste"
                @keydown="handleKeydown"
                placeholder="记录这个时刻的故事..."
              ></div>

              <!-- 隐藏的文件上传 -->
              <input
                ref="fileInput"
                type="file"
                accept="image/*"
                class="hidden-input"
                @change="handleImageUpload"
              />
            </div>
          </div>

          <!-- 元信息 -->
          <div class="form-section form-row">
            <div class="form-group">
              <label for="weather">天气</label>
              <select id="weather" v-model="form.weather" class="form-select">
                <option value="">选择天气</option>
                <option value="晴">晴</option>
                <option value="多云">多云</option>
                <option value="阴">阴</option>
                <option value="雨">雨</option>
                <option value="雪">雪</option>
              </select>
            </div>

            <div class="form-group">
              <label for="mood">心情</label>
              <select id="mood" v-model="form.mood" class="form-select">
                <option value="">选择心情</option>
                <option value="开心">开心</option>
                <option value="平静">平静</option>
                <option value="惬意">惬意</option>
                <option value="感动">感动</option>
                <option value="期待">期待</option>
                <option value="思念">思念</option>
                <option value="伤心">伤心</option>
                <option value="愤怒">愤怒</option>
                <option value="焦虑">焦虑</option>
                <option value="疲惫">疲惫</option>
              </select>
            </div>

            <div class="form-group">
              <label for="location">地点</label>
              <HandInput
                id="location"
                v-model="form.location"
                placeholder="记录地点"
              />
            </div>
          </div>

          <!-- 标签 -->
          <div class="form-section">
            <label>标签</label>
            <div class="tag-input-wrapper">
              <div class="tag-list">
                <span
                  v-for="(tag, index) in form.tags"
                  :key="index"
                  class="tag-item"
                >
                  #{{ tag }}
                  <button type="button" class="tag-remove" @click="removeTag(index)">×</button>
                </span>
              </div>
              <input
                v-model="tagInput"
                type="text"
                placeholder="输入标签后按回车添加"
                class="tag-input"
                @keydown.enter.prevent="addTag"
              />
            </div>
          </div>

          <!-- 可见性设置 -->
          <div class="form-section">
            <label>可见性</label>
            <div class="visibility-options">
              <label
                v-for="option in visibilityOptions"
                :key="option.value"
                class="visibility-option"
              >
                <input
                  type="radio"
                  v-model="form.visibility"
                  :value="option.value"
                />
                <span class="visibility-label">{{ option.label }}</span>
                <span class="visibility-desc">{{ option.desc }}</span>
              </label>
            </div>
          </div>
        </form>
      </HandCard>
    </div>
  </PaperTexture>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { toast } from '@/composables/useToast'
import { isTouchDevice } from '@/composables/useTouchGestures'
import type { BlogMedia, UploadResponse } from '@/types'
import { postApi, fileApi } from '@/api'
import HandButton from '@/components/base/HandButton.vue'
import HandInput from '@/components/base/HandInput.vue'
import HandCard from '@/components/base/HandCard.vue'
import HandLoading from '@/components/common/HandLoading.vue'
import PaperTexture from '@/components/decorative/PaperTexture.vue'
import Pin from '@/components/decorative/Pin.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isEdit = computed(() => route.name === 'PostUserEdit' && !!route.params.id)
const loading = ref(false)
const saving = ref(false)

// 特殊用户：只能看到这个权限的是用户1和100的用户
const isSpecialUser = computed(() => {
  const userId = userStore.user?.userId
  return userId === 1 || userId === 100
})

// 可见性选项
const visibilityOptions = computed(() => {
  const options = [
    { value: 'public', label: '公开', desc: '所有人可见' },
    { value: 'friends', label: '好友可见', desc: '仅好友可见' },
    { value: 'private', label: '私密', desc: '仅自己可见' }
  ]
  // 只有用户1和100可以看到PARTNER选项
  if (isSpecialUser.value) {
    options.push({ value: 'partner', label: '仅TA可见', desc: '仅对ta可见' })
  }
  return options
})

const fileInput = ref<HTMLInputElement | null>(null)
const tagInput = ref('')
const editorContent = ref<HTMLElement | null>(null)

// 用于存储内联图片映射关系
const inlineImages = ref<Map<string, BlogMedia>>(new Map())

// 触摸设备检测
const isMobile = ref(false)

// 内联图片触摸状态
let longPressTimer: ReturnType<typeof setTimeout> | null = null

const form = reactive({
  title: '',
  content: '',
  weather: '',
  mood: '',
  location: '',
  visibility: 'public',
  mediaList: [] as BlogMedia[],
  tags: [] as string[]
})

// 加载文章数据 (编辑模式)
async function loadPost() {
  if (!isEdit.value) return

  const postId = Number(route.params.id)
  loading.value = true

  try {
    const post = await postApi.getPost(postId)

    // 检查是否有权限编辑（必须是作者）
    if (post.userId !== userStore.user?.userId) {
      toast.warning('您没有权限编辑此文章')
      router.push('/')
      return
    }

    // 先设置 loading 为 false，让表单渲染出来
    loading.value = false

    // 然后在下一个 tick 填充表单（等待 DOM 更新）
    await nextTick()
    populateForm(post)
  } catch (err) {
    console.error('Failed to load post:', err)
    toast.error('加载文章失败')
    router.push('/')
    loading.value = false
  }
}

function populateForm(post: any) {
  form.title = post.title || ''
  form.content = post.content || ''
  form.weather = post.weather || ''
  form.mood = post.mood || ''
  form.location = post.location || ''
  form.visibility = post.visibility?.toLowerCase() || 'public'
  form.mediaList = post.mediaList || []
  form.tags = post.tagList?.map((t: any) => t.name) || []

  // 设置编辑器内容
  if (editorContent.value) {
    let content = post.content || ''

    console.log('编辑模式 - 原始内容:', content)
    console.log('编辑模式 - 媒体列表:', post.mediaList)

    // 使用临时 div 解析内容
    const tempDiv = document.createElement('div')
    tempDiv.innerHTML = content

    // 获取内容中所有现有的图片
    const existingImgs = tempDiv.querySelectorAll('img')
    const processedUrls = new Set<string>()

    // 重建图片映射，并为现有图片添加必要属性
    if (post.mediaList && post.mediaList.length > 0) {
      // 先建立 URL 到 media 的映射
      const urlToMedia = new Map<string, BlogMedia>()
      post.mediaList.forEach((media: BlogMedia) => {
        if (media.mediaUrl) {
          urlToMedia.set(media.mediaUrl, media)
          // 也添加不带查询参数的版本
          const urlWithoutQuery = media.mediaUrl.split('?')[0]
          if (urlWithoutQuery) {
            urlToMedia.set(urlWithoutQuery, media)
          }
        }
      })

      // 处理内容中已存在的图片
      existingImgs.forEach(img => {
        const src = img.getAttribute('src')
        if (!src) return

        // 尝试匹配媒体（完整URL或去掉查询参数后匹配）
        let matchingMedia = urlToMedia.get(src)
        if (!matchingMedia) {
          const srcWithoutQuery = src.split('?')[0]
          if (srcWithoutQuery) {
            matchingMedia = urlToMedia.get(srcWithoutQuery)
          }
        }

        // 也可以通过 URL 包含关系匹配
        if (!matchingMedia) {
          for (const media of post.mediaList) {
            if (src.includes(media.mediaUrl) || media.mediaUrl.includes(src) ||
                src.split('?')[0] === media.mediaUrl.split('?')[0]) {
              matchingMedia = media
              break
            }
          }
        }

        if (matchingMedia) {
          const imgId = `img_${matchingMedia.mediaId}`
          inlineImages.value.set(imgId, matchingMedia)

          // 记录已处理的 URL
          processedUrls.add(matchingMedia.mediaUrl)
          processedUrls.add(src)

          // 设置 data-id 和样式
          img.setAttribute('data-id', imgId)
          img.classList.add('inline-image')
          img.setAttribute('style', 'max-width: 250px; height: auto; border-radius: 8px; margin: 10px 0; cursor: pointer;')

          console.log('匹配并更新图片:', imgId, src)
        } else {
          // 内容中有图片但不在 mediaList 中，可能是用户新添加的或其他来源
          // 仍然添加样式
          img.classList.add('inline-image')
          img.setAttribute('style', 'max-width: 250px; height: auto; border-radius: 8px; margin: 10px 0; cursor: pointer;')
          console.log('发现未匹配的图片:', src)
        }
      })

      // 检查是否有 mediaList 中的图片完全不在内容中
      // 只有当内容中完全没有该图片时才添加
      post.mediaList.forEach((media: BlogMedia) => {
        if (!processedUrls.has(media.mediaUrl)) {
          // 再次确认内容中真的没有这个图片
          const contentHasImage = Array.from(existingImgs).some(img => {
            const src = img.getAttribute('src') || ''
            return src === media.mediaUrl ||
                   src.split('?')[0] === media.mediaUrl.split('?')[0] ||
                   src.includes(media.mediaUrl) ||
                   media.mediaUrl.includes(src)
          })

          if (!contentHasImage) {
            const imgId = `img_${media.mediaId}`
            inlineImages.value.set(imgId, media)
            const p = document.createElement('p')
            p.innerHTML = `<img src="${media.mediaUrl}" data-id="${imgId}" class="inline-image" style="max-width: 250px; height: auto; border-radius: 8px; margin: 10px 0; cursor: pointer;"/>`
            tempDiv.appendChild(p)
            console.log('在末尾添加缺失的图片:', imgId)
          }
        }
      })
    } else {
      // 没有 mediaList，但内容中可能有图片，添加样式
      existingImgs.forEach(img => {
        img.classList.add('inline-image')
        img.setAttribute('style', 'max-width: 250px; height: auto; border-radius: 8px; margin: 10px 0; cursor: pointer;')
      })
    }

    content = tempDiv.innerHTML
    console.log('编辑模式 - 处理后内容:', content)

    editorContent.value.innerHTML = content

    // 为所有图片添加删除事件和样式
    setupImageEventHandlers()
  }
}

// 为编辑器中的图片设置事件处理器
function setupImageEventHandlers() {
  if (!editorContent.value) return

  const images = editorContent.value.querySelectorAll('img')
  images.forEach((img) => {
    const imgElement = img as HTMLImageElement

    // 确保所有图片都有正确的样式
    imgElement.classList.add('inline-image')
    imgElement.style.cssText = 'max-width: 250px; height: auto; border-radius: 8px; margin: 10px 0; cursor: pointer;'

    // 如果图片没有 data-id，尝试从 mediaList 匹配
    if (!imgElement.getAttribute('data-id')) {
      const src = imgElement.getAttribute('src')
      if (src) {
        const matchingMedia = form.mediaList.find(m => m.mediaUrl === src)
        if (matchingMedia) {
          imgElement.setAttribute('data-id', `img_${matchingMedia.mediaId}`)
        }
      }
    }

    imgElement.addEventListener('dblclick', () => {
      confirmDeleteImage(imgElement)
    })

    let touchTimer: ReturnType<typeof setTimeout> | null = null
    imgElement.addEventListener('touchstart', (e) => {
      touchTimer = setTimeout(() => {
        e.preventDefault()
        confirmDeleteImage(imgElement)
      }, 600)
    }, { passive: false })

    imgElement.addEventListener('touchend', () => {
      if (touchTimer) {
        clearTimeout(touchTimer)
        touchTimer = null
      }
    })

    imgElement.addEventListener('touchmove', () => {
      if (touchTimer) {
        clearTimeout(touchTimer)
        touchTimer = null
      }
    })
  })
}

// 富文本编辑器相关函数
function triggerImageUpload() {
  fileInput.value?.click()
}

function handleImageUpload(e: Event) {
  const input = e.target as HTMLInputElement
  const files = input.files
  if (!files || !files[0]) return

  const file = files[0]
  if (file.size > 5 * 1024 * 1024) {
    toast.error(`图片 "${file.name}" 超过 5MB 限制`)
    return
  }

  fileApi.upload(file)
    .then((response: UploadResponse) => {
      const mediaItem: BlogMedia = {
        mediaId: Date.now() + Math.random(),
        postId: 0,
        mediaUrl: response.url,
        mediaType: 'image',
        rotation: Math.floor(Math.random() * 6) - 3,
        scale: 1,
        positionX: 0,
        positionY: 0,
        filterStyle: '',
        sortOrder: form.mediaList.length,
        createTime: new Date().toISOString(),
        zindex: form.mediaList.length
      }

      const imgId = `img_${mediaItem.mediaId}`
      inlineImages.value.set(imgId, mediaItem)
      form.mediaList.push(mediaItem)

      // 在光标位置插入图片
      insertImageAtCursor(response.url, imgId)
    })
    .catch((error) => {
      console.error('Upload failed:', error)
      toast.error(`图片 "${file.name}" 上传失败`)
    })

  input.value = ''
}

function insertImageAtCursor(imageUrl: string, imgId: string) {
  const selection = window.getSelection()
  if (!selection || !editorContent.value) return

  const range = selection.getRangeAt(0)
  if (!editorContent.value.contains(range.commonAncestorContainer)) {
    // 如果不在编辑器内，将光标移到编辑器末尾
    editorContent.value.focus()
    const newRange = document.createRange()
    newRange.selectNodeContents(editorContent.value)
    newRange.collapse(false)
    selection.removeAllRanges()
    selection.addRange(newRange)
  }

  const img = document.createElement('img')
  img.src = imageUrl
  img.setAttribute('data-id', imgId)
  img.className = 'inline-image'
  img.style.cssText = 'max-width: 250px; height: auto; border-radius: 8px; margin: 10px 0; cursor: pointer;'

  // 添加双击/长按删除功能
  img.addEventListener('dblclick', () => {
    confirmDeleteImage(img)
  })

  // 移动端长按删除
  let touchTimer: ReturnType<typeof setTimeout> | null = null
  img.addEventListener('touchstart', (e) => {
    touchTimer = setTimeout(() => {
      e.preventDefault()
      confirmDeleteImage(img)
    }, 600)
  }, { passive: false })

  img.addEventListener('touchend', () => {
    if (touchTimer) {
      clearTimeout(touchTimer)
      touchTimer = null
    }
  })

  img.addEventListener('touchmove', () => {
    if (touchTimer) {
      clearTimeout(touchTimer)
      touchTimer = null
    }
  })

  const currentRange = selection.getRangeAt(0)
  currentRange.deleteContents()
  currentRange.insertNode(img)
  currentRange.collapse(false)
  selection.removeAllRanges()
  selection.addRange(currentRange)

  updateContent()
}

// 确认删除图片
function confirmDeleteImage(img: HTMLImageElement) {
  if (confirm('删除这张图片？')) {
    const mediaId = img.getAttribute('data-id')
    if (mediaId) {
      inlineImages.value.delete(mediaId)
      const mediaIndex = form.mediaList.findIndex(m => `img_${m.mediaId}` === mediaId)
      if (mediaIndex !== -1) {
        form.mediaList.splice(mediaIndex, 1)
      }
    }
    img.remove()
    updateContent()
  }
}

function handleContentChange() {
  updateContent()
}

function handlePaste(e: ClipboardEvent) {
  e.preventDefault()
  const text = e.clipboardData?.getData('text/plain') || ''
  document.execCommand('insertText', false, text)
}

function handleKeydown(e: KeyboardEvent) {
  // 处理Enter键，尝试保持段落结构或插入<br>
  if (e.key === 'Enter') {
    if (e.shiftKey) {
      // Shift+Enter: 插入换行
      e.preventDefault()
      document.execCommand('insertLineBreak', false)
    } else {
      // Enter: 插入段落
      e.preventDefault()
      document.execCommand('insertParagraph', false)
    }
  }
}

function insertParagraph() {
  document.execCommand('insertParagraph', false)
}

function updateContent() {
  if (!editorContent.value) return

  // 获取编辑器中当前存在的所有图片
  const existingImages = editorContent.value.querySelectorAll('.inline-image')
  const existingImageIds = new Set<string>()

  // 收集当前编辑器中存在的图片 ID
  existingImages.forEach(img => {
    const imgId = img.getAttribute('data-id')
    if (imgId) {
      existingImageIds.add(imgId)
    }
  })

  // 同步 mediaList - 只保留编辑器中实际存在的图片
  form.mediaList = form.mediaList.filter(media => {
    const imgId = `img_${media.mediaId}`
    return existingImageIds.has(imgId)
  })

  // 同步 inlineImages Map
  const idsToRemove: string[] = []
  inlineImages.value.forEach((_, imgId) => {
    if (!existingImageIds.has(imgId)) {
      idsToRemove.push(imgId)
    }
  })
  idsToRemove.forEach(id => inlineImages.value.delete(id))

  // 直接保存 HTML 内容（保持图片和文字的顺序）
  // 不再将图片替换为标记，直接保存带有图片 URL 的 HTML
  let content = editorContent.value.innerHTML

  // 清理内联图片的额外属性，只保留必要的属性
  const tempDiv = document.createElement('div')
  tempDiv.innerHTML = content

  const imgs = tempDiv.querySelectorAll('img')
  imgs.forEach(img => {
    // 保留 src 和基本样式
    const src = img.getAttribute('src')
    const dataId = img.getAttribute('data-id')
    if (src) {
      // 创建一个干净的图片标签
      img.setAttribute('style', 'max-width: 100%; height: auto; border-radius: 8px; margin: 10px 0;')
      img.removeAttribute('class')
      if (dataId) {
        img.setAttribute('data-id', dataId)
      }
    }
  })

  form.content = tempDiv.innerHTML
}

// 标签管理
function addTag() {
  const tag = tagInput.value.trim()
  if (tag && !form.tags.includes(tag)) {
    form.tags.push(tag)
  }
  tagInput.value = ''
}

function removeTag(index: number) {
  form.tags.splice(index, 1)
}

// 发布
async function publish() {
  if (!form.title.trim()) {
    toast.error('请输入标题')
    return
  }
  if (!form.content.trim()) {
    toast.error('请输入内容')
    return
  }

  // 调试信息
  console.log('发布前的数据:', {
    title: form.title,
    content: form.content,
    tags: form.tags,
    tagList: form.tags.map((name) => ({ name }))
  })

  saving.value = true

  const postData = {
    title: form.title,
    content: form.content,
    weather: form.weather,
    mood: form.mood,
    location: form.location,
    visibility: form.visibility.toUpperCase(),
    status: 1, // 已发布
    userId: userStore.user?.userId,
    mediaList: form.mediaList,
    tagList: form.tags.map((name) => ({
      name: name
    }))
  }

  try {
    if (isEdit.value) {
      await postApi.updatePost(Number(route.params.id), postData)
    } else {
      await postApi.createPost(postData)
    }
    toast.success('发布成功！')
    router.push('/')
  } catch (err) {
    console.error('Failed to save post:', err)
    toast.error('保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

function goBack() {
  router.back()
}

onMounted(() => {
  // 检查登录状态
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  // 检测移动设备
  isMobile.value = isTouchDevice()

  loadPost()
})

onUnmounted(() => {
  // 清理定时器
  if (longPressTimer) {
    clearTimeout(longPressTimer)
  }
})
</script>

<style scoped lang="scss">
.post-new {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;

  &__card {
    padding: 40px 30px;
  }

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32px;
  }

  &__title {
    font-family: var(--font-handwriting);
    font-size: 2rem;
    color: var(--color-soft-purple);
    margin: 0;
  }

  &__actions {
    display: flex;
    gap: 12px;
  }

  &__form {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }

  .form-section {
    label {
      display: block;
      font-family: var(--font-handwriting);
      font-size: 1.1rem;
      color: var(--color-ink);
      margin-bottom: 10px;
    }
  }

  .form-row {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
  }

  .title-input {
    :deep(input) {
      font-size: 1.1rem;
      font-family: var(--font-handwriting);
    }
  }

  // 富文本编辑器
  .rich-editor {
    border: 1px dashed rgba(168, 140, 190, 0.4);
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.3);
    overflow: hidden;
    transition: all 0.2s;

    &:focus-within {
      border-color: var(--color-soft-purple);
      background: rgba(255, 255, 255, 0.5);
      box-shadow: 0 0 0 3px rgba(168, 140, 190, 0.1);
    }
  }

  .editor-toolbar {
    display: flex;
    align-items: center;
    padding: 8px 12px;
    background: rgba(168, 140, 190, 0.08);
    border-bottom: 1px dashed rgba(168, 140, 190, 0.3);
  }

  .toolbar-group {
    display: flex;
    gap: 4px;
  }

  .editor-content {
    min-height: 200px;
    padding: 16px;
    font-family: var(--font-body);
    font-size: 1rem;
    line-height: 1.8;
    outline: none;
    color: var(--color-ink);

    &:empty:before {
      content: attr(placeholder);
      color: var(--color-ink-light);
      opacity: 0.6;
      pointer-events: none;
    }

    // 内联图片样式
    :deep(.inline-image) {
      display: block;
      max-width: 280px;
      height: auto;
      border-radius: 8px;
      margin: 12px auto;
      box-shadow: 2px 4px 12px rgba(0, 0, 0, 0.1);
      cursor: pointer;
      transition: transform 0.2s, box-shadow 0.2s;

      &:hover {
        transform: scale(1.02);
        box-shadow: 3px 6px 16px rgba(0, 0, 0, 0.15);
      }
    }

    p {
      margin: 0 0 0.5em 0;
    }
  }

  .form-select {
    width: 100%;
    padding: 10px 14px;
    border: 1px dashed rgba(168, 140, 190, 0.4);
    border-radius: 8px;
    font-family: var(--font-body);
    font-size: 0.95rem;
    background: rgba(255, 255, 255, 0.5);
    cursor: pointer;
    transition: all 0.2s;

    &:focus {
      outline: none;
      border-color: var(--color-soft-purple);
      background: rgba(255, 255, 255, 0.8);
    }
  }

  .hidden-input {
    display: none;
  }

  // 标签输入 - 优化样式
  .tag-input-wrapper {
    border: 1px dashed rgba(168, 140, 190, 0.4);
    border-radius: 8px;
    padding: 12px 16px;
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    align-items: center;
    background: rgba(255, 255, 255, 0.3);
    transition: all 0.2s;
    min-height: 50px;

    &:focus-within {
      border-color: var(--color-soft-purple);
      background: rgba(255, 255, 255, 0.5);
      box-shadow: 0 0 0 3px rgba(168, 140, 190, 0.1);
    }

    .tag-list {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
    }

    .tag-item {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 5px 12px;
      background: var(--color-blush);
      border-radius: 16px;
      font-family: var(--font-body);
      font-size: 0.85rem;
      color: var(--color-ink);
      border: 1px solid rgba(168, 140, 190, 0.2);

      .tag-remove {
        background: none;
        border: none;
        padding: 0;
        cursor: pointer;
        color: var(--color-ink-light);
        font-size: 1rem;
        line-height: 1;
        transition: color 0.2s;

        &:hover {
          color: #c00;
        }
      }
    }

    .tag-input {
      flex: 1;
      min-width: 140px;
      border: none;
      outline: none;
      font-family: var(--font-body);
      font-size: 0.9rem;
      padding: 6px 0;
      background: transparent;
      color: var(--color-ink);

      &::placeholder {
        color: var(--color-ink-light);
        opacity: 0.6;
      }
    }
  }

  // 可见性设置
  .visibility-options {
    display: flex;
    gap: 16px;
  }

  .visibility-option {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    cursor: pointer;
    padding: 16px 20px;
    border: 1px dashed rgba(168, 140, 190, 0.4);
    border-radius: 12px;
    transition: all 0.2s;
    background: rgba(255, 255, 255, 0.3);
    flex: 1;
    text-align: center;

    &:hover {
      background: rgba(168, 140, 190, 0.1);
      border-color: var(--color-soft-purple);
    }

    &:has(input:checked) {
      border-color: var(--color-soft-purple);
      border-style: solid;
      background: rgba(168, 140, 190, 0.15);
    }

    input[type="radio"] {
      display: none;
    }

    .visibility-label {
      font-family: var(--font-handwriting);
      font-size: 1rem;
      color: var(--color-ink);
    }

    .visibility-desc {
      font-family: var(--font-body);
      font-size: 0.75rem;
      color: var(--color-ink-light);
    }
  }
}

// 响应式
@media (max-width: 768px) {
  .post-new {
    padding: 16px 12px;

    &__card {
      padding: 20px 16px;
    }

    &__header {
      flex-direction: column;
      gap: 16px;
      align-items: flex-start;
    }

    &__title {
      font-size: 1.6rem;
    }

    &__actions {
      width: 100%;
      display: flex;
      gap: 12px;

      // 触摸友好的按钮尺寸
      :deep(.hand-button) {
        min-height: 44px;
        padding: 12px 20px;
        font-size: 1rem;
      }
    }

    .form-row {
      grid-template-columns: 1fr;
    }

    .form-select {
      min-height: 48px;
      padding: 12px 14px;
      font-size: 1rem;
    }

    .visibility-options {
      flex-direction: column;
      gap: 12px;
    }

    .visibility-option {
      flex-direction: row;
      justify-content: flex-start;
      gap: 12px;
      padding: 16px 18px;
      min-height: 56px; // 触摸友好

      .visibility-label {
        min-width: 70px;
      }
    }

    .editor-toolbar {
      padding: 10px 14px;

      :deep(.hand-button) {
        min-width: 44px;
        min-height: 44px;
        font-size: 1.2rem;
      }
    }

    .editor-content {
      min-height: 180px;
      padding: 14px;
      font-size: 1rem;

      :deep(.inline-image) {
        max-width: 100%;
      }
    }

    .tag-input-wrapper {
      min-height: 56px;
      padding: 14px;

      .tag-input {
        min-height: 44px;
        font-size: 1rem;
      }

      .tag-item {
        padding: 8px 14px;
        font-size: 0.9rem;

        .tag-remove {
          width: 24px;
          height: 24px;
          font-size: 1.2rem;
        }
      }
    }
  }
}

// 小屏幕优化
@media (max-width: 480px) {
  .post-new {
    padding: 12px 8px;

    &__card {
      padding: 16px 12px;
    }

    &__title {
      font-size: 1.4rem;
    }

    .form-section label {
      font-size: 1rem;
    }
  }
}

// 触摸设备通用优化
@media (hover: none) and (pointer: coarse) {
  .post-new {
    // 所有可点击元素增大触摸区域
    .form-select,
    .date-input,
    .tag-input {
      min-height: 48px;
    }

    .visibility-option {
      min-height: 56px;
    }

    // 内联图片提示
    .editor-content {
      :deep(.inline-image) {
        &::after {
          content: '长按删除';
          position: absolute;
          bottom: -20px;
          left: 50%;
          transform: translateX(-50%);
          font-size: 0.75rem;
          color: var(--color-ink-light);
          opacity: 0;
          transition: opacity 0.2s;
        }

        &:active::after {
          opacity: 1;
        }
      }
    }
  }
}
</style>
