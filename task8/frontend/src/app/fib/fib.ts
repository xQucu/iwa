import { Component, computed, signal, Signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { MatFormField, MatInput } from "@angular/material/input";

@Component({
  selector: "app-fib",
  imports: [MatFormField, MatInput, FormsModule],
  templateUrl: "./fib.html",
  styleUrl: "./fib.css",
})
export class Fib {
  number = signal(0);
  result: Signal<BigInt> = computed(() => {
    const n = this.number();
    if (n <= 0) {
      return BigInt(0);
    }
    if (n === 1) {
      return BigInt(1);
    }

    let a = BigInt(0);
    let b = BigInt(1);
    for (let i = 2; i <= n; i++) {
      const temp = BigInt(a + b);
      a = b;
      b = temp;
    }
    return b;
  });
}
