import { Routes } from "@angular/router";
import { StudentForm } from "./student-form/student-form";
import { Calc } from "./calc/calc";
import { Fib } from "./fib/fib";
import { Quad } from "./quad/quad";
import { User } from "./user/user";
import { RoleGuard } from "./guards/role-guard";
import { Admin } from "./admin/admin";
import { authGuard } from "./guards/auth-guard";
import { Login } from "./login/login";
import { Register } from "./register/register";
import { Helloworld } from "./helloworld/helloworld";

export const routes: Routes = [
  { path: "", component: StudentForm, pathMatch: "full" },
  { path: "calc", component: Calc },
  { path: "fib", component: Fib },
  { path: "quad", component: Quad },
  { path: "hello", component: Helloworld, title: "Helloworld" },
  {
    path: "user",
    component: User,
    canActivate: [RoleGuard],
    data: { roles: ["ROLE_USER", "ROLE_ADMIN"] },
  },
  {
    path: "admin",
    component: Admin,
    canActivate: [authGuard],
    data: { roles: ["ROLE_ADMIN"] },
  },
  { path: "auth/login", component: Login },
  { path: "signup", component: Register },
  { path: "", redirectTo: "home", pathMatch: "full" },
];
