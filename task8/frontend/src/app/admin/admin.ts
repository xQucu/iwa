import {Component, inject, OnInit, signal} from '@angular/core';
import {UserService} from '../services/user-service';

@Component({
  selector: 'app-admin',
  imports: [],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class Admin implements OnInit {
  board= signal<string>('');
  errorMessage = signal<string>('');

  private userService = inject(UserService);

  ngOnInit() {
    this.userService.getAdminPage().subscribe({
      next:(data) => {
        this.board.set(data);
      },
      error: (error) => {
        this.errorMessage.set(`${error.status}: ${JSON.parse(error.error).message}`);
      }
    });
  }}


