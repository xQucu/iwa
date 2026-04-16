import { ComponentFixture, TestBed } from "@angular/core/testing";

import { Fib } from "./fib";

describe("Fib", () => {
  let component: Fib;
  let fixture: ComponentFixture<Fib>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Fib],
    }).compileComponents();

    fixture = TestBed.createComponent(Fib);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
