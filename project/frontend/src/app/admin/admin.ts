import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { UserService } from '../services/user-service';
import { User } from '../models/user';

@Component({
  selector: 'app-admin',
  imports: [RouterLink],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class Admin implements OnInit {
  users = signal<User[]>([]);
  errorMessage = signal<string>('');
  successMessage = signal<string>('');

  private userService = inject(UserService);

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.userService.getUsers().subscribe({
      next: (data) => this.users.set(data),
      error: (err) => {
        this.errorMessage.set('Failed to load users.');
        console.error(err);
      }
    });
  }

  hasRole(user: User, roleName: string): boolean {
    return user.roles.some((role) => role.name === roleName);
  }

  toggleRole(user: User) {
    let targetRole = '';
    if (this.hasRole(user, 'ROLE_STUDENT')) {
      targetRole = 'teacher';
    } else if (this.hasRole(user, 'ROLE_TEACHER')) {
      targetRole = 'student';
    } else {
      return; // Can't toggle admin
    }

    this.userService.updateUserRole(user.id, targetRole).subscribe({
      next: (updatedUser) => {
        if (updatedUser) {
          this.users.update((list) =>
            list.map((u) => (u.id === updatedUser.id ? updatedUser : u))
          );
          this.successMessage.set(`Updated role for ${user.username} successfully.`);
          setTimeout(() => this.successMessage.set(''), 3000);
        } else {
          this.errorMessage.set('Failed to update user role.');
        }
      },
      error: (err) => console.error(err)
    });
  }
}
