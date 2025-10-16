import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransfersPage } from './transfers-page';

describe('TransfersPage', () => {
  let component: TransfersPage;
  let fixture: ComponentFixture<TransfersPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransfersPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TransfersPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
