import {Component, inject, OnInit, signal} from '@angular/core';
import {UserService} from '../services/user-service';

@Component({
  selector: 'app-user',
  imports: [],
  templateUrl: './user.html',
  styleUrl: './user.css',
})
export class User implements OnInit {
  board = signal<string>('');
  errorMessage = signal<string>('');

  private userService = inject(UserService);

  ngOnInit() {
    this.userService.getUserPage().subscribe({
      next: (data) =>
      {
        this.board.set(data);
      }
      ,
      error: (error) => {
        this.errorMessage.set(`${error.status}: ${JSON.parse(error.error).message}`);
      }
    });
  }}

