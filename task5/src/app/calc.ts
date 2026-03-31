import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatInput } from '@angular/material/input';
import { MatFormField, MatOption, MatSelect } from '@angular/material/select';

@Component({
  selector: 'app-calc',
  template: `<div class="calc">
    <h1>Calculator</h1>
    <div>
      <mat-form-field class="example-full-width">
        <input matInput type="number" [(ngModel)]="leftNumber" />
      </mat-form-field>
      <mat-form-field>
        <mat-select [(value)]="operation">
          <mat-option value="+">+</mat-option>
          <mat-option value="-">-</mat-option>
          <mat-option value="*">*</mat-option>
          <mat-option value="/">/</mat-option>
        </mat-select>
      </mat-form-field>
      <mat-form-field class="example-full-width">
        <input matInput type="number" [(ngModel)]="rightNumber" />
      </mat-form-field>
    </div>
    <button matButton="elevated" (click)="calculate()">Calculate</button>
    @if (isValid()) {
      <p>Result: {{ result() }}</p>
    } @else {
      <p>Ensure that the calculation is allowed</p>
    }
  </div>`,
  styleUrl: './app.css',
  imports: [MatSelect, MatButton, MatFormField, MatOption, MatInput, FormsModule],
})
export class Calc {
  leftNumber = signal(0);
  rightNumber = signal(0);
  operation = signal('+');
  result = signal(0);

  isValid = computed(() => {
    const r = this.result();
    return r !== null && isFinite(r);
  });

  calculate() {
    switch (this.operation()) {
      case '+':
        this.result.set(this.leftNumber() + this.rightNumber());
        break;
      case '-':
        this.result.set(this.leftNumber() - this.rightNumber());
        break;
      case '*':
        this.result.set(this.leftNumber() * this.rightNumber());
        break;
      case '/':
        if (this.rightNumber() != 0) {
          this.result.set(this.leftNumber() / this.rightNumber());
        }
        break;
    }
  }
}
