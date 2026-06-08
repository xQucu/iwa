import { Component, inject, signal } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { TokenStorageService } from "./auth/token-storage-service";

@Component({
  selector: "app-root",
  imports: [RouterOutlet],
  templateUrl: "./app.html",
  styleUrl: "./app.css",
})

export class App {
  protected readonly title = signal("task5");
  private roles = signal<string[]>([]);
  authority = signal<string>("");

  private tokenStorage = inject(TokenStorageService);

  ngOnInit() {
    console.log("init");
    const token = this.tokenStorage.getToken();
    if (token && token !== '{}') {
      console.log(token);
      this.roles.set(this.tokenStorage.getAuthorities());
      this.roles().every((role) => {
        if (role === "ROLE_TEACHER") {
          this.authority.set("teacher");
          return false;
        }
        this.authority.set("student");
        return true;
      });
    }
  }
}

