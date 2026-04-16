import { ComponentFixture, TestBed } from "@angular/core/testing";

import { Quad } from "./quad";

describe("Quad", () => {
  let component: Quad;
  let fixture: ComponentFixture<Quad>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Quad],
    }).compileComponents();

    fixture = TestBed.createComponent(Quad);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
