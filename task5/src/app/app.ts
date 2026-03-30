import { Component, signal } from '@angular/core';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { RouterOutlet } from '@angular/router';
import { Calc } from './calc';
import { Fib } from './fib';
import { Quad } from './quad';
import { MatGridList, MatGridTile } from '@angular/material/grid-list';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MatSlideToggle, MatGridList, MatGridTile, Calc, Fib, Quad],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('task5');
}
