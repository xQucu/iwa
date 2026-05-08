import {Component, inject} from '@angular/core';
import {TokenStorageService} from '../auth/token-storage-service';
import {NgTemplateOutlet} from '@angular/common';

@Component({
  selector: 'app-helloworld',
  imports: [
    NgTemplateOutlet
  ],
  templateUrl: './helloworld.html',
  styleUrl: './helloworld.css',
})
export class Helloworld {
  info: any;

  private tokenStorage = inject(TokenStorageService);

  ngOnInit() {
    this.info = {
      token: this.tokenStorage.getToken(),
      username: this.tokenStorage.getUsername(),
      authorities: this.tokenStorage.getAuthorities()
    };
  }

  logout() {
    this.tokenStorage.signOut();
    window.location.reload();
  }
}

