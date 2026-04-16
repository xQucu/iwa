import { Component, computed, signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { MatButton } from "@angular/material/button";
import { MatInput } from "@angular/material/input";
import { MatFormField, MatOption, MatSelect } from "@angular/material/select";

@Component({
  selector: "app-calc",
  imports: [
    MatSelect,
    MatButton,
    MatFormField,
    MatOption,
    MatInput,
    FormsModule,
  ],
  templateUrl: "./calc.html",
  styleUrl: "./calc.css",
})
export class Calc {
  leftNumber = signal(0);
  rightNumber = signal(0);
  operation = signal("+");
  result = signal(0);

  isValid = computed(() => {
    const r = this.result();
    return r !== null && isFinite(r);
  });

  calculate() {
    switch (this.operation()) {
      case "+":
        this.result.set(this.leftNumber() + this.rightNumber());
        break;
      case "-":
        this.result.set(this.leftNumber() - this.rightNumber());
        break;
      case "*":
        this.result.set(this.leftNumber() * this.rightNumber());
        break;
      case "/":
        if (this.rightNumber() != 0) {
          this.result.set(this.leftNumber() / this.rightNumber());
        }
        break;
    }
  }
}
