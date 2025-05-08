import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

type ButtonType = 'default' | 'primary' | 'secondary' | 'informative' | 'warning' | 'danger' | 'submit';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.css']
})
export class ButtonComponent {
  @Input() type: ButtonType = 'primary';
  @Input() disabled: boolean = false;
  @Input() isLoading: boolean = false;
  @Input() fullWidth: boolean = false;
  
  @Output() onClicked = new EventEmitter<MouseEvent>();
  @Output() btnClick = new EventEmitter<void>();
  
  OnClickButton(event: MouseEvent): void {
    // Skip if disabled or loading
    if (this.disabled || this.isLoading) {
      event.preventDefault();
      event.stopPropagation();
      return;
    }
    
    this.onClicked.emit(event);
    this.btnClick.emit();
    
    if (this.type !== 'submit') {
      event.preventDefault();
    }
  }
  
  get buttonClass(): string {
    const classes = ['btn'];
    classes.push(`btn-${this.type}`);
    if (this.fullWidth) {
      classes.push('btn-full-width');
    }
    return classes.join(' ');
  }
}