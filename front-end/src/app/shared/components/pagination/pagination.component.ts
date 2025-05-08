import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { ButtonComponent } from '../button/button.component';

@Component({
  selector: 'app-pagination',
  imports: [CommonModule, ButtonComponent, CommonModule],
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.css',
})
export class PaginationComponent implements OnChanges {
  @Input() totalItemNums: number = 0;
  @Input() currentPage: number = 1;
  @Input() itemPerPage: number = 5;
  @Output() pageChange = new EventEmitter<number>();

  totalPageNum: number = 0;

  ngOnChanges(changes: SimpleChanges): void {
    this.computeTotalPages();
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPageNum && page !== this.currentPage) {
      this.pageChange.emit(page);
    }
  }

  private computeTotalPages(): void {
    this.totalPageNum = Math.ceil(this.totalItemNums / this.itemPerPage);
  }

  get paginationRange(): (number | string)[] {
    const pages: (number | string)[] = [];
    const total = this.totalPageNum;
    const current = this.currentPage;

    if (total <= 1) return [1]; // Only one page

    // Always show page 1
    pages.push(1);

    // Show "..." if needed
    if (current > 3) {
      pages.push('...');
    }

    // Show pages around current page
    for (let i = current - 1; i <= current + 1; i++) {
      if (i > 1 && i < total) {
        pages.push(i);
      }
    }

    // Show "..." before last if needed
    if (current < total - 2) {
      pages.push('...');
    }

    // Always show last page
    if (total > 1) {
      pages.push(total);
    }

    return pages;
  }

  toNumber(p: string | number): number {
    const num = typeof p === 'string' ? Number(p) : p;
    return isNaN(num) ? 0 : num;
  }
}
