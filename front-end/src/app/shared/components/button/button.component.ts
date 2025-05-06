import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-button',
  imports: [CommonModule],
  templateUrl: './button.component.html',
  styleUrl: './button.component.css',
})
export class ButtonComponent {
  @Input() type:
    | 'default'
    | 'primary'
    | 'secondary'
    | 'informative'
    | 'warning'
    | 'danger' = 'default';

  @Input() size: 'small' | 'medium' | 'large' = 'medium';

  @Input() fontWeight: 'normal' | 'bold' = 'normal';

  @Output() onClicked = new EventEmitter<MouseEvent>();

  OnClickButton(event: MouseEvent) {
    this.onClicked.emit(event);
  }

  get buttonClass() {
    return {
      btn: true,
      'btn-default': this.type === 'default',
      'btn-primary': this.type === 'primary',
      'btn-secondary': this.type === 'secondary',
      'btn-informative': this.type === 'informative',
      'btn-warning': this.type === 'warning',
      'btn-danger': this.type === 'danger',
      'btn-small': this.size === 'small',
      'btn-medium': this.size === 'medium',
      'btn-large': this.size === 'large',
      'font-bold': this.fontWeight === 'bold',
      'font-normal': this.fontWeight === 'normal',
    };
  }
}
