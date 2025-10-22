import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreditCardRequest } from './credit-card-request';

describe('CreditCardRequest', () => {
  let component: CreditCardRequest;
  let fixture: ComponentFixture<CreditCardRequest>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreditCardRequest]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreditCardRequest);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
