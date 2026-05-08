import { Component, inject, signal } from "@angular/core";
import { RouterOutlet, RouterLink } from "@angular/router";
import { Navbar } from "./navbar/navbar";
import { TokenStorageService } from "./auth/token-storage-service";

@Component({
  selector: "app-root",
  imports: [RouterOutlet, Navbar, RouterLink],
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
    if (this.tokenStorage.getToken()) {
      console.log(this.tokenStorage.getToken());
      this.roles.set(this.tokenStorage.getAuthorities());
      this.roles().every((role) => {
        if (role === "ROLE_ADMIN") {
          this.authority.set("admin");
          return false;
        }
        this.authority.set("user");
        return true;
      });
    }
  }
}
