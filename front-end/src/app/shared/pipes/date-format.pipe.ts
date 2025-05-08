import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dateFormat',
  standalone: true
})
export class DateFormatPipe implements PipeTransform {
  transform(value: string | Date, format: string = 'medium'): string {
    if (!value) return '';
    
    const date = typeof value === 'string' ? new Date(value) : value;
    
    if (format === 'timeAgo') {
      // Time ago logic would go here
      return 'Recently';
    }
    
    return new Date(value).toLocaleDateString();
  }
}