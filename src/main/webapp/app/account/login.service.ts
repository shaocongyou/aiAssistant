export default class LoginService {
  private readonly emit: (event: string, ...args: any[]) => void;

  constructor({ emit }: { emit: (event: string, ...args: any[]) => void }) {
    this.emit = emit;
  }

  /*
  public openLogin(): void { this.emit('bv::show::modal', 'login-page'); } 这段代码的作用是触发一个事件，显示一个模态框（Modal），通常用于打开登录页面或登录弹窗。

以下是详细解释：

1.  **this.emit('bv::show::modal', 'login-page')**
    **this.emit**：这是一个事件发射方法，用于触发一个自定义事件。
    **'bv::show::modal'**：这是 BootstrapVue 框架中预定义的事件名称，用于显示模态框。
    **'login-page'**：这是模态框的 ID 或标识符，指定要显示的模态框。
*/
  public openLogin(): void {
    this.emit('bv::show::modal', 'login-page');
  }

  public hideLogin(): void {
    this.emit('bv::hide::modal', 'login-page');
  }
}
