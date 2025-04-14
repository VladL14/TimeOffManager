import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { NgIf } from '@angular/common';   // 🔥 Adaugă importul ăsta!

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NgIf],  // 🔥 Adaugă și NgIf aici!
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'frontend';
  messageFromBackend: string = '';

  constructor(private http: HttpClient) {}

  getHelloMessage() {
    this.http.get('api/hello', { responseType: 'text' })
      .subscribe({
        next: (response) => {
          this.messageFromBackend = response;
        },
        error: (error) => {
          console.error('Eroare la apel backend', error);
        }
      });
  }
}
