import { Routes } from "@angular/router";
import { StudentForm } from "./student-form/student-form";
import { Calc } from "./calc/calc";
import { Fib } from "./fib/fib";
import { Quad } from "./quad/quad";

export const routes: Routes = [
  { path: "", component: StudentForm, pathMatch: "full" },
  { path: "calc", component: Calc },
  { path: "fib", component: Fib },
  { path: "quad", component: Quad },
];
