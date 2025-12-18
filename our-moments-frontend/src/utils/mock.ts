import type { BlogPost, User, Category, Tag, BlogMedia } from '@/types'

/**
 * Mock 数据生成工具
 * 用于开发和测试阶段
 */

// 示例用户
export const mockUsers: User[] = [
  {
    userId: 1,
    username: 'xiaoming',
    nickname: '小明',
    avatar: 'https://api.dicebear.com/7.x/adventurer/svg?seed=xiaoming',
    bio: '喜欢记录生活的点点滴滴，用文字和图片留住美好的瞬间。',
    createTime: '2024-01-01T00:00:00',
    updateTime: '2024-01-01T00:00:00'
  },
  {
    userId: 2,
    username: 'xiaohong',
    nickname: '小红',
    avatar: 'https://api.dicebear.com/7.x/adventurer/svg?seed=xiaohong',
    bio: '热爱生活，热爱分享。',
    createTime: '2024-01-15T00:00:00',
    updateTime: '2024-01-15T00:00:00'
  }
]

// 示例分类
export const mockCategories: Category[] = [
  {
    categoryId: 1,
    name: '日常',
    iconUrl: '',
    sortOrder: 1,
    createTime: '2024-01-01T00:00:00'
  },
  {
    categoryId: 2,
    name: '美食',
    iconUrl: '',
    sortOrder: 2,
    createTime: '2024-01-01T00:00:00'
  },
  {
    categoryId: 3,
    name: '旅行',
    iconUrl: '',
    sortOrder: 3,
    createTime: '2024-01-01T00:00:00'
  },
  {
    categoryId: 4,
    name: '心情',
    iconUrl: '',
    sortOrder: 4,
    createTime: '2024-01-01T00:00:00'
  }
]

// 示例标签
export const mockTags: Tag[] = [
  { tagId: 1, name: '周末', createTime: '2024-01-01T00:00:00' },
  { tagId: 2, name: '美好', createTime: '2024-01-01T00:00:00' },
  { tagId: 3, name: '阳光', createTime: '2024-01-01T00:00:00' },
  { tagId: 4, name: '咖啡', createTime: '2024-01-01T00:00:00' },
  { tagId: 5, name: '读书', createTime: '2024-01-01T00:00:00' },
  { tagId: 6, name: '散步', createTime: '2024-01-01T00:00:00' }
]

// 示例媒体
function generateMockMedia(postId: number, count: number = 3): BlogMedia[] {
  const media: BlogMedia[] = []
  for (let i = 0; i < count; i++) {
    media.push({
      mediaId: postId * 100 + i,
      postId,
      mediaUrl: `https://picsum.photos/seed/${postId}-${i}/400/300`,
      mediaType: 'image',
      rotation: Math.floor(Math.random() * 6) - 3, // -3 到 3 度随机旋转
      scale: 1,
      positionX: 0,
      positionY: 0,
      filterStyle: '',
      sortOrder: i,
      createTime: new Date().toISOString(),
      zindex: i
    })
  }
  return media
}

// 示例文章
export const mockPosts: BlogPost[] = [
  {
    postId: 1,
    userId: 1,
    categoryId: 1,
    title: '周末的下午茶时光',
    content: `今天是个温暖的周末，阳光透过窗帘洒进来，泡了一杯热茶，听着喜欢的音乐，翻开一本书。

这样简单的午后，却让人感到无比幸福。生活的美好，往往藏在这些平凡的瞬间里。

窗外的树叶被微风轻轻吹动，发出沙沙的声响，仿佛在诉说着什么。我放下书本，静静地看着窗外，享受这难得的宁静。`,
    weather: '晴',
    mood: '惬意',
    location: '家里',
    status: 1,
    createTime: '2024-12-15T14:30:00',
    updateTime: '2024-12-15T14:30:00',
    mediaList: generateMockMedia(1, 2),
    tagList: [mockTags[0]!, mockTags[1]!, mockTags[4]!],
    category: mockCategories[0]!,
    author: mockUsers[0]!
  },
  {
    postId: 2,
    userId: 1,
    categoryId: 2,
    title: '发现一家超棒的咖啡店',
    content: `今天和朋友约在市中心逛街，无意中发现了一家藏在小巷子里的咖啡店。

店面不大，但装修得很温馨。木质的桌椅，暖黄的灯光，墙上挂满了各种手绘画。

点了一杯拿铁和一块提拉米苏，味道超级棒！店主是一对年轻的夫妻，非常热情。下次一定要再来！`,
    weather: '多云',
    mood: '开心',
    location: '城市咖啡馆',
    status: 1,
    createTime: '2024-12-14T16:00:00',
    updateTime: '2024-12-14T16:00:00',
    mediaList: generateMockMedia(2, 4),
    tagList: [mockTags[3]!, mockTags[1]!],
    category: mockCategories[1]!,
    author: mockUsers[0]!
  },
  {
    postId: 3,
    userId: 2,
    categoryId: 3,
    title: '海边的日落',
    content: `终于等到了假期，来到了心心念念的海边。

傍晚的时候，我坐在沙滩上，看着太阳慢慢沉入海平线。天空被染成了橙红色，云彩像是被火焰点燃了一般。

海浪轻轻拍打着沙滩，发出有节奏的声音。这一刻，所有的烦恼都消失了，只剩下心中的平静。

旅行的意义，大概就是这样吧——让心灵得到休憩，让生活重新充满能量。`,
    weather: '晴',
    mood: '感动',
    location: '海边',
    status: 1,
    createTime: '2024-12-10T18:30:00',
    updateTime: '2024-12-10T18:30:00',
    mediaList: generateMockMedia(3, 3),
    tagList: [mockTags[1]!, mockTags[2]!],
    category: mockCategories[2]!,
    author: mockUsers[1]!
  },
  {
    postId: 4,
    userId: 1,
    categoryId: 4,
    title: '雨天的思绪',
    content: `窗外下着淅淅沥沥的小雨，空气中弥漫着泥土的清香。

这样的天气总让人变得感性。听着雨声，思绪不由自主地飘向了远方。

想起了很多往事，想起了那些曾经一起走过的日子。虽然时光已经远去，但那些美好的记忆，永远留在心底。

人生就像这场雨，有时候会让人感到忧伤，但雨后总会有彩虹。`,
    weather: '雨',
    mood: '思念',
    location: '窗边',
    status: 1,
    createTime: '2024-12-08T20:00:00',
    updateTime: '2024-12-08T20:00:00',
    mediaList: generateMockMedia(4, 1),
    tagList: [mockTags[1]!],
    category: mockCategories[3]!,
    author: mockUsers[0]!
  }
]

/**
 * 生成随机文章数据
 */
export function generateMockPost(id: number): BlogPost {
  const randomUser = mockUsers[Math.floor(Math.random() * mockUsers.length)]!
  const randomCategory = mockCategories[Math.floor(Math.random() * mockCategories.length)]!
  const randomTags = mockTags
    .sort(() => Math.random() - 0.5)
    .slice(0, Math.floor(Math.random() * 3) + 1)

  const titles = [
    '今天的小确幸',
    '一个人的午后',
    '和朋友的聚会',
    '发现了一家好店',
    '周末的小冒险',
    '记录美好瞬间'
  ]

  const weathers = ['晴', '多云', '阴', '雨', '雪']
  const moods = ['开心', '平静', '惬意', '感动', '期待']

  return {
    postId: id,
    userId: randomUser.userId,
    categoryId: randomCategory.categoryId,
    title: titles[Math.floor(Math.random() * titles.length)]!,
    content: '这是一段随机生成的内容。生活中总有很多值得记录的瞬间，用文字和图片把它们保存下来，日后回看，会是一份珍贵的回忆。',
    weather: weathers[Math.floor(Math.random() * weathers.length)]!,
    mood: moods[Math.floor(Math.random() * moods.length)]!,
    location: '某个地方',
    status: 1,
    createTime: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
    updateTime: new Date().toISOString(),
    mediaList: generateMockMedia(id, Math.floor(Math.random() * 4) + 1),
    tagList: randomTags,
    category: randomCategory,
    author: randomUser
  }
}

/**
 * 生成指定数量的 Mock 文章列表
 */
export function generateMockPosts(count: number): BlogPost[] {
  const posts: BlogPost[] = []
  for (let i = 1; i <= count; i++) {
    posts.push(generateMockPost(i + 100))
  }
  return posts
}
