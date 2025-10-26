import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BankingAccount } from './banking-account';

describe('BankingAccount', () => {
  let component: BankingAccount;
  let fixture: ComponentFixture<BankingAccount>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BankingAccount]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BankingAccount);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
