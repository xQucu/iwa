import { Component, computed, signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { MatButton } from "@angular/material/button";
import { MatInput, MatLabel } from "@angular/material/input";
import { MatFormField } from "@angular/material/select";

@Component({
  selector: "app-quad",
  imports: [MatButton, MatFormField, MatInput, FormsModule, MatLabel],
  templateUrl: "./quad.html",
  styleUrl: "./quad.css",
})
export class Quad {
  a = signal(0);
  b = signal(0);
  c = signal(0);
  result1 = signal(0);
  result2 = signal(0);
  isSecondResultSet = signal(false);

  isValid = computed(() => {
    const delta = this.b() * this.b() - 4 * this.a() * this.c();
    return this.a() !== 0 && delta >= 0;
  });

  calculate() {
    const delta = this.b() * this.b() - 4 * this.a() * this.c();
    if (this.a() === 0 || delta < 0) {
      return;
    }

    if (delta === 0) {
      const root = -this.b() / (2 * this.b());
      this.result1.set(root);
      this.isSecondResultSet.set(false);
    } else {
      const root1 = (-this.b() + Math.sqrt(delta)) / (2 * this.a());
      const root2 = (-this.b() - Math.sqrt(delta)) / (2 * this.a());
      this.result1.set(root1);
      this.result2.set(root2);
      this.isSecondResultSet.set(true);
    }
  }
}
