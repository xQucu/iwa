export class Student {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  number: string;

  constructor(
    firstName: string,
    lastName: string,
    email: string,
    number: string,
  ) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.number = number;
  }
}
