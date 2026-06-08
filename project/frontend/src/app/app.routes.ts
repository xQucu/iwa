import { Routes } from "@angular/router";
import { StudentForm } from "./student-form/student-form";
import { User } from "./user/user";
import { RoleGuard } from "./guards/role-guard";
import { Admin } from "./admin/admin";
import { Teacher } from "./teacher/teacher";
import { Login } from "./login/login";
import { Register } from "./register/register";

export const routes: Routes = [
  { path: "", component: StudentForm, pathMatch: "full" },
  {
    path: "user",
    component: User,
    canActivate: [RoleGuard],
    data: { roles: ["ROLE_STUDENT"] },
  },
  {
    path: "teacher",
    component: Teacher,
    canActivate: [RoleGuard],
    data: { roles: ["ROLE_TEACHER"] },
  },
  {
    path: "admin",
    component: Admin,
    canActivate: [RoleGuard],
    data: { roles: ["ROLE_ADMIN"] },
  },
  { path: "auth/login", component: Login },
  { path: "signup", component: Register },
  { path: "", redirectTo: "home", pathMatch: "full" },
];


