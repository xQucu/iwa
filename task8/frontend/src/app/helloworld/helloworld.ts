import {Component, inject, signal} from '@angular/core';
import {TokenStorageService} from '../auth/token-storage-service';
import {NgTemplateOutlet} from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-helloworld',
  imports: [
    NgTemplateOutlet,
    FormsModule
  ],
  templateUrl: './helloworld.html',
  styleUrl: './helloworld.css',
})
export class Helloworld {
  info: any;

  onenterText = signal('');
  oninputText = signal('');
  onhoverText = signal('');
  mousePosition = signal({x: 0, y: 0});

  private tokenStorage = inject(TokenStorageService);

  ngOnInit() {
    this.info = {
      token: this.tokenStorage.getToken(),
      username: this.tokenStorage.getUsername(),
      authorities: this.tokenStorage.getAuthorities()
    };
  }

  onEnter() {
    this.onenterText.set('Mouse entered!');
  }

  onInput(value: string) {
    this.oninputText.set(value);
  }

  onHover() {
    this.onhoverText.set('Mouse hovered!');
  }

  onMouseMove(event: MouseEvent) {
    this.mousePosition.set({x: event.clientX, y: event.clientY});
  }

  logout() {
    this.tokenStorage.signOut();
    window.location.reload();
  }
}

