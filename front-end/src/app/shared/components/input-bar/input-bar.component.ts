import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-input-bar',
  imports: [CommonModule],
  templateUrl: './input-bar.component.html',
  styleUrl: './input-bar.component.css',
})
export class InputBarComponent {
  @Input() label?: string;
  @Input() type: 'text' | 'number' | 'email' | 'checkbox' | 'password' = 'text';
  @Input() value: string | number = '';
  @Input() placeholder: string = '';
  @Input() hasError: boolean = false;
  @Output() valueChange = new EventEmitter<string>();

  get inputId(): string {
    return this.label || `input-${Math.random().toString(36).slice(2, 9)}`;
  }

  onInputChange(event: Event) {
    const input = event.target as HTMLInputElement;
    const val = input?.value ?? '';
    this.value = val;
    this.valueChange.emit(this.value);
  }
}
