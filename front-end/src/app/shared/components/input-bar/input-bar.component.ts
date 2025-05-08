import { Component, Input, forwardRef, Output, EventEmitter } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-input-bar',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './input-bar.component.html',
  styleUrls: ['./input-bar.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => InputBarComponent),
      multi: true
    }
  ]
})
export class InputBarComponent implements ControlValueAccessor {
  @Input() label: string = '';
  @Input() type: string = 'text';
  @Input() placeholder: string = '';
  @Input() hasError: boolean = false;
  @Input() errorMessage: string = ''; 
  @Input() inputId: string = 'input-' + Math.random().toString(36).substring(2, 9);
  @Output() valueChange = new EventEmitter<string>();
  
  value: any = '';
  disabled: boolean = false;
  
  onChange: any = () => {};
  onTouched: any = () => {};
  
  writeValue(value: any): void {
    this.value = value ?? '';
  }
  
  registerOnChange(fn: any): void {
    this.onChange = fn;
  }
  
  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }
  
  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }
  
  onInputChange(event: any): void {
    this.value = event.target.value;
    this.onChange(this.value);
    this.onTouched();
    this.valueChange.emit(this.value);
  }
}
