export type ToastType = 'success' | 'error' | 'warning' | 'info'

interface ToastOptions {
  message: string
  type?: ToastType
  duration?: number
}

const iconMap: Record<ToastType, string> = {
  success: '✓',
  error: '✕',
  warning: '!',
  info: 'i'
}

function createToastElement(options: ToastOptions) {
  const { message, type = 'info', duration = 3000 } = options

  // 创建容器
  const container = document.createElement('div')
  container.className = 'hand-toast-container'
  container.innerHTML = `
    <div class="hand-toast hand-toast--${type}">
      <span class="hand-toast__icon">${iconMap[type]}</span>
      <span class="hand-toast__message">${message}</span>
    </div>
  `

  // 添加样式
  const style = document.createElement('style')
  style.textContent = `
    .hand-toast-container {
      position: fixed;
      top: 80px;
      left: 50%;
      transform: translateX(-50%);
      z-index: 9999;
      animation: toast-in 0.3s ease;
    }

    .hand-toast-container.leaving {
      animation: toast-out 0.3s ease forwards;
    }

    .hand-toast {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 14px 24px;
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(8px);
      border-radius: 12px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
      font-family: 'LXGW WenKai', 'Noto Serif SC', serif;
      font-size: 0.95rem;
      color: #4A4A4A;
      border: 1px solid rgba(168, 140, 190, 0.2);
    }

    .hand-toast--success {
      border-left: 4px solid #B5EAD7;
    }
    .hand-toast--success .hand-toast__icon {
      background: #B5EAD7;
      color: white;
    }

    .hand-toast--error {
      border-left: 4px solid #e57373;
    }
    .hand-toast--error .hand-toast__icon {
      background: #e57373;
      color: white;
    }

    .hand-toast--warning {
      border-left: 4px solid #ffb74d;
    }
    .hand-toast--warning .hand-toast__icon {
      background: #ffb74d;
      color: white;
    }

    .hand-toast--info {
      border-left: 4px solid #957DAD;
    }
    .hand-toast--info .hand-toast__icon {
      background: #957DAD;
      color: white;
    }

    .hand-toast__icon {
      width: 22px;
      height: 22px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 0.75rem;
      font-weight: bold;
    }

    .hand-toast__message {
      max-width: 300px;
    }

    @keyframes toast-in {
      from {
        opacity: 0;
        transform: translateX(-50%) translateY(-20px);
      }
      to {
        opacity: 1;
        transform: translateX(-50%) translateY(0);
      }
    }

    @keyframes toast-out {
      from {
        opacity: 1;
        transform: translateX(-50%) translateY(0);
      }
      to {
        opacity: 0;
        transform: translateX(-50%) translateY(-10px);
      }
    }
  `

  document.head.appendChild(style)
  document.body.appendChild(container)

  // 自动移除
  setTimeout(() => {
    container.classList.add('leaving')
    setTimeout(() => {
      container.remove()
      style.remove()
    }, 300)
  }, duration)

  return container
}

export function useToast() {
  const show = (options: ToastOptions | string) => {
    const opts = typeof options === 'string' ? { message: options } : options
    createToastElement(opts)
  }

  const success = (message: string, duration?: number) => {
    show({ message, type: 'success', duration })
  }

  const error = (message: string, duration?: number) => {
    show({ message, type: 'error', duration })
  }

  const warning = (message: string, duration?: number) => {
    show({ message, type: 'warning', duration })
  }

  const info = (message: string, duration?: number) => {
    show({ message, type: 'info', duration })
  }

  return {
    show,
    success,
    error,
    warning,
    info
  }
}

// 全局 toast 函数
export const toast = {
  show: (options: ToastOptions | string) => {
    const opts = typeof options === 'string' ? { message: options } : options
    createToastElement(opts)
  },
  success: (message: string, duration?: number) => {
    createToastElement({ message, type: 'success', duration })
  },
  error: (message: string, duration?: number) => {
    createToastElement({ message, type: 'error', duration })
  },
  warning: (message: string, duration?: number) => {
    createToastElement({ message, type: 'warning', duration })
  },
  info: (message: string, duration?: number) => {
    createToastElement({ message, type: 'info', duration })
  }
}
