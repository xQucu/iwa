import {Component, inject, signal} from '@angular/core';
import {SignupInfo} from '../auth/signup-info';
import {AuthService} from '../auth/auth-service';
import {FormsModule} from '@angular/forms';
import {NgTemplateOutlet} from '@angular/common';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [FormsModule, NgTemplateOutlet, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})

export class Register {
  form: any = {};
  signupInfo?: SignupInfo;
  isSignedUp = signal(false);
  isSignUpFailed = signal(false);
  errorMessage = signal<string>('');

  private authService = inject(AuthService);

  onSubmit() {
    console.log(this.form);

    this.signupInfo = new SignupInfo(
      this.form.username,
      this.form.password,
      [this.form.role || 'student']
    );


    this.authService.signUp(this.signupInfo).subscribe({
      next: (data) =>
      {
        console.log(data);
        this.isSignedUp.set(true);
        this.isSignUpFailed.set(false);
      }
      ,
      error: (error) => {
        console.log(error);
        this.errorMessage.set(error.error.message);
        this.isSignUpFailed.set(true);
      }
    });
  }

}







