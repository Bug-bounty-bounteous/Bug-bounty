import { CommonModule } from '@angular/common';
import { Component, EventEmitter, forwardRef, Output } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-search-bar',
  imports: [CommonModule],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SearchBarComponent),
      multi: true,
    },
  ],
})
export class SearchBarComponent implements ControlValueAccessor {
  searchIconUrl: string = 'assets/images/search.png';
  closeIconUrl: string = 'assets/images/close.png';

  @Output() valueChange = new EventEmitter<string>();

  value: string = '';
  onChange = (value: any) => {};
  onTouched = () => {};
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

  clearSearch() {
    this.value = '';
    this.onChange(this.value);
    this.valueChange.emit('');
  }
}
