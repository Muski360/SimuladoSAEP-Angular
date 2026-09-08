import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { Agendamento, AdminSession, Cliente, Estufa, LoginPayload } from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8081/api';

  login(payload: LoginPayload): Observable<AdminSession> {
    return this.http.post<AdminSession>(`${this.baseUrl}/admin/login`, payload);
  }

  listarClientes(busca = ''): Observable<Cliente[]> {
    const params = busca.trim() ? new HttpParams().set('busca', busca.trim()) : undefined;
    return this.http.get<Cliente[]>(`${this.baseUrl}/clientes`, { params });
  }

  criarCliente(cliente: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(`${this.baseUrl}/clientes`, cliente);
  }

  atualizarCliente(id: number, cliente: Cliente): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.baseUrl}/clientes/${id}`, cliente);
  }

  excluirCliente(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/clientes/${id}`);
  }

  listarEstufas(busca = ''): Observable<Estufa[]> {
    const params = busca.trim() ? new HttpParams().set('busca', busca.trim()) : undefined;
    return this.http.get<Estufa[]>(`${this.baseUrl}/estufas`, { params });
  }

  criarEstufa(estufa: Estufa): Observable<Estufa> {
    return this.http.post<Estufa>(`${this.baseUrl}/estufas`, estufa);
  }

  atualizarEstufa(id: number, estufa: Estufa): Observable<Estufa> {
    return this.http.put<Estufa>(`${this.baseUrl}/estufas/${id}`, estufa);
  }

  excluirEstufa(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/estufas/${id}`);
  }

  listarAgendamentos(data = ''): Observable<Agendamento[]> {
    const params = data ? new HttpParams().set('data', data) : undefined;
    return this.http.get<Agendamento[]>(`${this.baseUrl}/agendamentos`, { params });
  }

  criarAgendamento(agendamento: Agendamento): Observable<Agendamento> {
    return this.http.post<Agendamento>(`${this.baseUrl}/agendamentos`, agendamento);
  }

  atualizarAgendamento(id: number, agendamento: Agendamento): Observable<Agendamento> {
    return this.http.put<Agendamento>(`${this.baseUrl}/agendamentos/${id}`, agendamento);
  }

  excluirAgendamento(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/agendamentos/${id}`);
  }

  mensagemDeErro(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (typeof error.error?.mensagem === 'string') {
        return error.error.mensagem;
      }
      if (error.status === 0) {
        return 'Nao foi possivel conectar ao backend. Verifique se a API esta em execucao.';
      }
      if (error.status === 409) {
        return 'A estufa ja esta ocupada nesse horario.';
      }
      if (error.status === 401) {
        return 'E-mail ou senha invalidos.';
      }
    }

    return 'Nao foi possivel concluir a operacao. Tente novamente.';
  }
}
