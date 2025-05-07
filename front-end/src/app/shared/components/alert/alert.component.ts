import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent {
  @Input() type: 'success' | 'info' | 'warning' | 'danger' = 'info';
  @Input() message: string = '';
  visible: boolean = true;

  close(): void {
    this.visible = false;
  }
}
