export class SignupInfo {

  username: string;
  role: string[];
  password: string;

  constructor(username: string, password: string, role: string[] = ['student']) {
    this.username = username;
    this.role = role;
    this.password = password;
  }
}


