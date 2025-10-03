import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreditCardDashboard } from './credit-card-dashboard';

describe('CreditCardDashboard', () => {
  let component: CreditCardDashboard;
  let fixture: ComponentFixture<CreditCardDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreditCardDashboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreditCardDashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
