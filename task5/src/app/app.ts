import { Component, signal } from '@angular/core';
import { MatGridList, MatGridTile } from '@angular/material/grid-list';
import { RouterOutlet } from '@angular/router';
import { Calc } from './calc';
import { Fib } from './fib';
import { Quad } from './quad';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MatGridList, MatGridTile, Fib, Quad, Calc],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('task5');
}
