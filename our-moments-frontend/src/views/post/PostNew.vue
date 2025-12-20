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
                    variant="ghost"
                    size="sm"
                    title="添加图片"
                    @click="triggerImageUpload"
                  >
                    📷
                  </HandButton>
                  <HandButton
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
              <label class="visibility-option">
                <input type="radio" v-model="form.visibility" value="public" />
                <span class="visibility-label">公开</span>
                <span class="visibility-desc">所有人可见</span>
              </label>
              <label class="visibility-option">
                <input type="radio" v-model="form.visibility" value="friends" />
                <span class="visibility-label">好友可见</span>
                <span class="visibility-desc">仅好友可见</span>
              </label>
              <label class="visibility-option">
                <input type="radio" v-model="form.visibility" value="private" />
                <span class="visibility-label">私密</span>
                <span class="visibility-desc">仅自己可见</span>
              </label>
            </div>
          </div>
        </form>
      </HandCard>
    </div>
  </PaperTexture>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
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

const fileInput = ref<HTMLInputElement | null>(null)
const tagInput = ref('')
const editorContent = ref<HTMLElement | null>(null)

// 用于存储内联图片映射关系
const inlineImages = ref<Map<string, BlogMedia>>(new Map())

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
      alert('您没有权限编辑此文章')
      router.push('/')
      return
    }
    populateForm(post)
  } catch (err) {
    console.error('Failed to load post:', err)
    alert('加载文章失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

function populateForm(post: any) {
  form.title = post.title
  form.weather = post.weather || ''
  form.mood = post.mood || ''
  form.location = post.location || ''
  form.visibility = post.visibility || 'public'
  form.mediaList = post.mediaList || []
  form.tags = post.tagList?.map((t: any) => t.name) || []

  // 设置编辑器内容
  nextTick(() => {
    if (editorContent.value) {
      editorContent.value.innerHTML = post.content || ''
      // 重建图片映射
      if (post.mediaList) {
        post.mediaList.forEach((media: BlogMedia) => {
          const imgId = `img_${media.mediaId}`
          inlineImages.value.set(imgId, media)
          // 替换图片标记为实际的img标签
          editorContent.value!.innerHTML = editorContent.value!.innerHTML.replace(
            `<img-src="${imgId}"/>`,
            `<img src="${media.mediaUrl}" data-id="${imgId}" class="inline-image" style="max-width: 250px; height: auto; border-radius: 8px; margin: 10px 0;"/>`
          )
        })
      }
    }
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
    alert(`图片 "${file.name}" 超过 5MB 限制`)
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
      alert(`图片 "${file.name}" 上传失败`)
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

  // 添加双击删除功能
  img.addEventListener('dblclick', () => {
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
  })

  const currentRange = selection.getRangeAt(0)
  currentRange.deleteContents()
  currentRange.insertNode(img)
  currentRange.collapse(false)
  selection.removeAllRanges()
  selection.addRange(currentRange)

  updateContent()
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

  // 获取纯文本内容用于后端保存
  let content = editorContent.value.innerHTML

  // 替换图片标签为特殊标记，后端可以识别和解析
  const images = editorContent.value.querySelectorAll('.inline-image')
  images.forEach(img => {
    const imgId = img.getAttribute('data-id')
    if (imgId) {
      content = content.replace(img.outerHTML, `<img-src="${imgId}"/>`)
    }
  })

  form.content = content
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
    alert('请输入标题')
    return
  }
  if (!form.content.trim()) {
    alert('请输入内容')
    return
  }

  saving.value = true

  const postData = {
    title: form.title,
    content: form.content,
    weather: form.weather,
    mood: form.mood,
    location: form.location,
    visibility: form.visibility,
    status: 1, // 已发布
    userId: userStore.user?.userId,
    mediaList: form.mediaList,
    tagList: form.tags.map((name, index) => ({
      tagId: index + 1,
      name: name,
      createTime: new Date().toISOString()
    }))
  }

  try {
    if (isEdit.value) {
      await postApi.updatePost(Number(route.params.id), postData)
    } else {
      await postApi.createPost(postData)
    }
    router.push('/')
  } catch (err) {
    console.error('Failed to save post:', err)
    alert('保存失败，请稍后重试')
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
  loadPost()
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
    padding: 20px 16px;

    &__card {
      padding: 24px 20px;
    }

    &__header {
      flex-direction: column;
      gap: 16px;
      align-items: flex-start;
    }

    .form-row {
      grid-template-columns: 1fr;
    }

    .visibility-options {
      flex-direction: column;
      gap: 12px;
    }

    .visibility-option {
      flex-direction: row;
      justify-content: flex-start;
      gap: 12px;
      padding: 14px 18px;

      .visibility-label {
        min-width: 70px;
      }
    }
  }
}
</style>
