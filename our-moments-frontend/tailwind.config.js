/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // 背景色
        'paper': '#F9F5F0',
        'paper-dark': '#F0EAD6',
        // 线条与文字
        'ink': '#4A4A4A',
        'ink-light': '#8C8C8C',
        // 点缀色（低饱和）
        'dusty-pink': '#E0BBE4',
        'soft-purple': '#957DAD',
        'mauve': '#D291BC',
        'peach': '#FEC8D8',
        'blush': '#FFDFD3',
        'mint': '#B5EAD7',
      },
      fontFamily: {
        'handwriting': ['"Ma Shan Zheng"', '"ZCOOL XiaoWei"', 'cursive'],
        'serif': ['"Noto Serif SC"', '"Source Han Serif SC"', 'serif'],
        'body': ['"LXGW WenKai"', '"Noto Serif SC"', 'serif'],
      },
      boxShadow: {
        'warm': '2px 4px 10px rgba(210, 145, 188, 0.2)',
        'warm-lg': '4px 8px 20px rgba(210, 145, 188, 0.25)',
        'paper': '2px 3px 8px rgba(74, 74, 74, 0.1)',
      },
      borderRadius: {
        'hand': '255px 15px 225px 15px/15px 225px 15px 255px',
        'hand-sm': '15px 255px 15px 225px/255px 15px 225px 15px',
      },
      animation: {
        'wiggle': 'wiggle 0.3s ease-in-out',
        'float': 'float 3s ease-in-out infinite',
      },
      keyframes: {
        wiggle: {
          '0%, 100%': { transform: 'rotate(-1deg)' },
          '50%': { transform: 'rotate(1deg)' },
        },
        float: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-5px)' },
        },
      },
    },
  },
  plugins: [],
}
