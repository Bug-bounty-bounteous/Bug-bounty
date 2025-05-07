import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  forwardRef,
  Input,
  Output,
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-input-bar',
  imports: [CommonModule],
  templateUrl: './input-bar.component.html',
  styleUrl: './input-bar.component.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => InputBarComponent),
      multi: true,
    },
  ],
})
export class InputBarComponent implements ControlValueAccessor {
  @Input() label?: string;
  @Input() type: 'text' | 'number' | 'email' | 'password' = 'text';
  @Input() placeholder: string = '';
  @Input() hasError: boolean = false;
  value: string = '';
  @Output() valueChange = new EventEmitter<string>();

  onChange = (value: any) => {};
  onTouched = () => {};

  get inputId(): string {
    return this.label || `input-${Math.random().toString(36).slice(2, 9)}`;
  }

  writeValue(value: any): void {
    this.value = value;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  onInputChange(event: Event) {
    const input = event.target as HTMLInputElement;
    this.value = input.value;
    this.onChange(this.value);
    this.valueChange.emit(this.value);
  }
}
