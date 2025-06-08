import { Component } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HttpClientModule, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected title = 'jogo-bolas';
  public tubos: string[][] = [];
  public mensagem: string = '';
  public tuboSelecionado: number | null = null;
  private apiUrl = 'http://localhost:4567';

  public mostrarPopupVitoria = false;
  public movimentosVitoria = 0;
  public movimentos = 0;

  constructor(private http: HttpClient) {
    this.estadoJogo();
  }

  novoJogo() {
    this.http.post<any>(`${this.apiUrl}/game/new`, {}).subscribe({
      next: (res) => {
        this.tubos = res.tubes || [];
        this.mensagem = res.message || 'Novo jogo iniciado!';
        this.tuboSelecionado = null;
        this.mostrarPopupVitoria = false;
        this.movimentos = 0;
      },
      error: () => {
        this.mensagem = 'Erro ao iniciar novo jogo.';
      }
    });
  }

  estadoJogo() {
    this.http.get<any>(`${this.apiUrl}/game/state`).subscribe({
      next: (res) => {
        this.tubos = res.tubes || [];
        this.mensagem = res.message || '';
        if (res.isVictory) {
          this.mostrarPopupVitoria = true;
          this.movimentosVitoria = res.moveCount || 0;
        } else {
          this.mostrarPopupVitoria = false;
        }
      },
      error: () => {
        this.mensagem = 'Erro ao obter estado do jogo.';
      }
    });
  }

  selecionarTubo(i: number) {
    if (this.tuboSelecionado === null) {
      this.tuboSelecionado = i;
      this.mensagem = 'Selecione o tubo de destino.';
    } else if (i !== this.tuboSelecionado) {
      this.moverBola(this.tuboSelecionado, i);
      this.tuboSelecionado = null;
    } else {
      this.tuboSelecionado = i;
      this.mensagem = 'Selecione o tubo de destino.';
    }
  }

  moverBola(from: number, to: number) {
    this.http.post<any>(`${this.apiUrl}/game/move`, {
      fromTube: from,
      toTube: to
    }).subscribe({
      next: (res) => {
        this.tubos = res.tubes || [];
        this.mensagem = res.message || 'Movimento realizado!';
        if (res.isVictory) {
          this.mostrarPopupVitoria = true;
          this.movimentosVitoria = res.moveCount || 0;
        }
        this.movimentos = res.moveCount || (this.movimentos + 1);
      },
      error: (err) => {
        this.mensagem = err.error?.message || 'Movimento inválido!';
        this.estadoJogo();
      }
    });
  }

  fecharPopupVitoria() {
    this.mostrarPopupVitoria = false;
  }

  corBola(bola: string): string {
    const cores: Record<string, string> = {
      'Red': 'red',
      'Blue': 'blue',
      'Green': 'green',
      'Yellow': 'yellow',
      'Purple': 'purple',
      'Orange': 'orange',
    };
    return cores[bola] || 'gray';
  }
}
