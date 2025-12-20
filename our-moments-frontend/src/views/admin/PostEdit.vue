<template>
  <div class="post-edit">
    <header class="post-edit__header">
      <h1 class="post-edit__title">{{ isEdit ? '✏️ 编辑文章' : '📝 新建文章' }}</h1>
      <div class="post-edit__actions">
        <HandButton variant="ghost" size="sm" @click="goBack">
          取消
        </HandButton>
        <HandButton variant="outline" size="sm" @click="saveDraft" :disabled="saving">
          存为草稿
        </HandButton>
        <HandButton variant="primary" size="sm" @click="publish" :disabled="saving">
          {{ saving ? '保存中...' : '发布' }}
        </HandButton>
      </div>
    </header>

    <!-- 加载状态 -->
    <HandLoading v-if="loading" text="正在加载文章..." />

    <!-- 编辑表单 -->
    <div v-else class="post-edit__form">
      <!-- 标题 -->
      <div class="form-section">
        <label for="title">文章标题</label>
        <HandInput
          id="title"
          v-model="form.title"
          placeholder="请输入文章标题"
          class="title-input"
        />
      </div>

      <!-- 内容 -->
      <div class="form-section">
        <label for="content">文章内容</label>
        <textarea
          id="content"
          v-model="form.content"
          placeholder="在这里写下你的故事..."
          class="content-textarea"
          rows="12"
        ></textarea>
        <p class="form-hint">支持换行，每段会自动添加首行缩进</p>
      </div>

      <!-- 图片上传 -->
      <div class="form-section">
        <label>图片</label>
        <div class="image-upload">
          <div class="image-list">
            <div
              v-for="(media, index) in form.mediaList"
              :key="media.mediaId || index"
              class="image-item"
            >
              <div class="image-preview">
                <img :src="media.mediaUrl" :alt="`图片 ${index + 1}`" />
              </div>
              <HandButton
                variant="ghost"
                size="sm"
                class="remove-btn"
                @click="removeImage(index)"
              >
                ×
              </HandButton>
            </div>

            <!-- 上传按钮 -->
            <div class="image-upload-btn" @click="triggerUpload">
              <span class="icon">📷</span>
              <span class="text">添加图片</span>
            </div>
          </div>

          <input
            ref="fileInput"
            type="file"
            accept="image/*"
            multiple
            class="hidden-input"
            @change="handleFileSelect"
          />
          <p class="form-hint">支持 JPG、PNG 格式，单张图片不超过 5MB（OSS 对接中，目前使用本地预览）</p>
        </div>
      </div>

      <!-- 元信息 -->
      <div class="form-section form-row">
        <div class="form-group">
          <label for="weather">天气</label>
          <select id="weather" v-model="form.weather" class="form-select">
            <option value="">选择天气</option>
            <option value="晴">☀️ 晴</option>
            <option value="多云">⛅ 多云</option>
            <option value="阴">☁️ 阴</option>
            <option value="雨">🌧️ 雨</option>
            <option value="雪">❄️ 雪</option>
          </select>
        </div>

        <div class="form-group">
          <label for="mood">心情</label>
          <select id="mood" v-model="form.mood" class="form-select">
            <option value="">选择心情</option>
            <option value="开心">😊 开心</option>
            <option value="平静">😌 平静</option>
            <option value="惬意">☺️ 惬意</option>
            <option value="感动">🥹 感动</option>
            <option value="期待">🤩 期待</option>
            <option value="思念">💭 思念</option>
          </select>
        </div>

        <div class="form-group">
          <label for="location">位置</label>
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
              <button class="tag-remove" @click="removeTag(index)">×</button>
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
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { BlogMedia, UploadResponse } from '@/types'
import { mockPosts } from '@/utils/mock'
import { postApi, fileApi } from '@/api'
import HandButton from '@/components/base/HandButton.vue'
import HandInput from '@/components/base/HandInput.vue'
import HandLoading from '@/components/common/HandLoading.vue'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const loading = ref(false)
const saving = ref(false)

const fileInput = ref<HTMLInputElement | null>(null)
const tagInput = ref('')

const form = reactive({
  title: '',
  content: '',
  weather: '',
  mood: '',
  location: '',
  mediaList: [] as BlogMedia[],
  tags: [] as string[]
})

// 加载文章数据 (编辑模式)
async function loadPost() {
  if (!isEdit.value) return

  const postId = Number(route.params.id)
  loading.value = true

  try {
    // 尝试调用后端 API
    const post = await postApi.getPost(postId)
    populateForm(post)
  } catch (err) {
    // 如果 API 调用失败，使用 Mock 数据
    console.warn('API call failed, using mock data')
    const post = mockPosts.find(p => p.postId === postId)
    if (post) {
      populateForm(post)
    }
  } finally {
    loading.value = false
  }
}

function populateForm(post: any) {
  form.title = post.title
  form.content = post.content
  form.weather = post.weather
  form.mood = post.mood
  form.location = post.location
  form.mediaList = post.mediaList || []
  form.tags = post.tagList?.map((t: any) => t.name) || []
}

// 图片上传
function triggerUpload() {
  fileInput.value?.click()
}

function handleFileSelect(e: Event) {
  const input = e.target as HTMLInputElement
  const files = input.files
  if (!files) return

  Array.from(files).forEach(file => {
    if (file.size > 5 * 1024 * 1024) {
      alert(`图片 "${file.name}" 超过 5MB 限制`)
      return
    }

    // Show upload progress
    const progressCallback = (percent: number) => {
      console.log(`Upload progress: ${percent}%`)
    }

    // Upload file to server
    fileApi.upload(file, progressCallback)
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
        form.mediaList.push(mediaItem)
      })
      .catch((error) => {
        console.error('Upload failed:', error)
        alert(`图片 "${file.name}" 上传失败`)
      })
  })

  input.value = ''
}

function removeImage(index: number) {
  form.mediaList.splice(index, 1)
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

// 保存操作
async function saveDraft() {
  await savePost(0)
}

async function publish() {
  if (!form.title.trim()) {
    alert('请输入文章标题')
    return
  }
  if (!form.content.trim()) {
    alert('请输入文章内容')
    return
  }
  await savePost(1)
}

async function savePost(status: number) {
  saving.value = true

  const postData = {
    title: form.title,
    content: form.content,
    weather: form.weather,
    mood: form.mood,
    location: form.location,
    status,
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
    router.push('/admin/posts')
  } catch (err) {
    console.warn('API call failed, simulating success')
    // Mock 模式下直接跳转
    router.push('/admin/posts')
  } finally {
    saving.value = false
  }
}

function goBack() {
  router.back()
}

onMounted(() => {
  loadPost()
})
</script>

<style scoped lang="scss">
.post-edit {
  max-width: 900px;

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32px;
  }

  &__title {
    font-family: var(--font-handwriting);
    font-size: 1.8rem;
    color: var(--color-ink);
    margin: 0;
  }

  &__actions {
    display: flex;
    gap: 12px;
  }

  &__form {
    background: white;
    padding: 32px;
    border-radius: 8px;
    box-shadow: 2px 4px 12px rgba(0, 0, 0, 0.08);
  }

  .form-section {
    margin-bottom: 28px;

    label {
      display: block;
      font-family: var(--font-body);
      font-size: 0.9rem;
      color: var(--color-ink);
      margin-bottom: 8px;
      font-weight: 500;
    }

    .form-hint {
      font-size: 0.8rem;
      color: var(--color-ink-light);
      margin-top: 8px;
    }
  }

  .form-row {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;

    .form-group {
      label {
        margin-bottom: 8px;
      }
    }
  }

  .title-input {
    :deep(input) {
      font-size: 1.1rem;
      font-family: var(--font-handwriting);
    }
  }

  .content-textarea {
    width: 100%;
    padding: 16px;
    border: 1px solid var(--color-ink-light);
    border-radius: 4px;
    font-family: var(--font-body);
    font-size: 1rem;
    line-height: 1.8;
    resize: vertical;
    min-height: 200px;

    &:focus {
      outline: none;
      border-color: var(--color-soft-purple);
    }
  }

  .form-select {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid var(--color-ink-light);
    border-radius: 4px;
    font-family: var(--font-body);
    font-size: 0.95rem;
    background: white;
    cursor: pointer;

    &:focus {
      outline: none;
      border-color: var(--color-soft-purple);
    }
  }

  .hidden-input {
    display: none;
  }

  // 图片上传
  .image-upload {
    .image-list {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
    }

    .image-item {
      position: relative;

      .image-preview {
        width: 120px;
        height: 120px;
        background: white;
        padding: 8px;
        box-shadow: 2px 3px 8px rgba(0, 0, 0, 0.1);
        border-radius: 2px;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .remove-btn {
        position: absolute;
        top: -8px;
        right: -8px;
        width: 24px;
        height: 24px;
        padding: 0;
        border-radius: 50%;
        background: white;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        color: #c00;
        font-size: 1rem;
      }
    }

    .image-upload-btn {
      width: 120px;
      height: 120px;
      border: 2px dashed var(--color-ink-light);
      border-radius: 4px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        border-color: var(--color-soft-purple);
        background: rgba(0, 0, 0, 0.02);
      }

      .icon {
        font-size: 1.5rem;
        margin-bottom: 4px;
      }

      .text {
        font-size: 0.8rem;
        color: var(--color-ink-light);
      }
    }
  }

  // 标签输入
  .tag-input-wrapper {
    border: 1px solid var(--color-ink-light);
    border-radius: 4px;
    padding: 8px 12px;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    align-items: center;

    &:focus-within {
      border-color: var(--color-soft-purple);
    }

    .tag-list {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
    }

    .tag-item {
      display: inline-flex;
      align-items: center;
      gap: 4px;
      padding: 4px 10px;
      background: var(--color-paper);
      border-radius: 12px;
      font-size: 0.85rem;
      color: var(--color-ink);

      .tag-remove {
        background: none;
        border: none;
        padding: 0;
        cursor: pointer;
        color: var(--color-ink-light);
        font-size: 1rem;
        line-height: 1;

        &:hover {
          color: #c00;
        }
      }
    }

    .tag-input {
      flex: 1;
      min-width: 120px;
      border: none;
      outline: none;
      font-family: var(--font-body);
      font-size: 0.9rem;
      padding: 4px 0;
    }
  }
}

// 响应式
@media (max-width: 768px) {
  .post-edit {
    &__header {
      flex-direction: column;
      gap: 16px;
      align-items: flex-start;
    }

    &__form {
      padding: 20px;
    }

    .form-row {
      grid-template-columns: 1fr;
    }
  }
}
</style>
