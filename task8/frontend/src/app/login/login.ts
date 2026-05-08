import {Component, inject, OnInit, signal} from '@angular/core';
import {LoginInfo} from '../auth/login-info';
import {AuthService} from '../auth/auth-service';
import {TokenStorageService} from '../auth/token-storage-service';
import {FormsModule} from '@angular/forms';
import {NgTemplateOutlet} from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [FormsModule, NgTemplateOutlet],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {
  form: any = {};
  token?: string;
  isLoggedIn = signal(false);
  isLoginFailed = signal(false);
  errorMessage = signal<string>('');
  roles: string[] = [];
  private loginInfo?: LoginInfo;

  private authService = inject(AuthService);
  private tokenStorage = inject(TokenStorageService);

  // older approach/syntax for DI that still works
  // constructor(private authService: AuthService, private tokenStorage: TokenStorageService) { }

  ngOnInit() {
    if (this.tokenStorage.getToken() != null && this.tokenStorage.getToken() != '{}') {
      this.isLoggedIn.set(true);
      this.roles = this.tokenStorage.getAuthorities();
    }
  }

  onSubmit() {
    console.log(this.form);

    this.loginInfo = new LoginInfo(this.form.username, this.form.password);

    this.authService.attemptAuth(this.loginInfo).subscribe({
      next: (data) => {
        this.tokenStorage.saveToken(data.accessToken || '{}');
        this.tokenStorage.saveUsername(data.username || '{}');
        this.tokenStorage.saveAuthorities(data.authorities || []);

        this.isLoginFailed.set(false);
        this.isLoggedIn.set(true);
        this.token = this.tokenStorage.getToken();
        this.roles = this.tokenStorage.getAuthorities();
        this.reloadPage();
      }
      ,
      error: (error) => {
        console.log(error);
        this.errorMessage.set(error.error.message);
        this.isLoginFailed.set(true);
      }
    });
  }

  reloadPage() {
    window.location.reload();
  }

}







