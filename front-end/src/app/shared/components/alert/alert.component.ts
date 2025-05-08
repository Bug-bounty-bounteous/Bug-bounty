import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-alert',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent implements OnInit {
  @Input() type: 'success' | 'info' | 'warning' | 'danger' = 'info';
  @Input() message: string = '';
  @Input() dismissible: boolean = true;
  @Input() autoClose: number = 0; // Time in milliseconds, 0 = no auto close
  
  visible: boolean = true;
  private timer: any;

  ngOnInit(): void {
    if (this.autoClose > 0) {
      this.timer = setTimeout(() => {
        this.close();
      }, this.autoClose);
    }
  }
  
  ngOnDestroy(): void {
    if (this.timer) {
      clearTimeout(this.timer);
    }
  }

  close(): void {
    this.visible = false;
  }
}