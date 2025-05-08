import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UnlockCaptchaComponent } from './unlock-captcha.component';

describe('UnlockCaptchaComponent', () => {
  let component: UnlockCaptchaComponent;
  let fixture: ComponentFixture<UnlockCaptchaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnlockCaptchaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UnlockCaptchaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
