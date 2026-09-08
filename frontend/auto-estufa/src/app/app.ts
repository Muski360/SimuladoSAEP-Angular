import { DecimalPipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from './core/api.service';
import { Agendamento, Cliente, Estufa } from './core/models';

type View = 'dashboard' | 'clientes' | 'agendamentos';
type ToastType = 'success' | 'error' | 'info';

@Component({
  imports: [FormsModule, DecimalPipe],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App implements OnInit {
  private readonly api = inject(ApiService);

  protected readonly activeView = signal<View>('dashboard');
  protected readonly loggedIn = signal(Boolean(sessionStorage.getItem('auto-estufa-admin')));
  protected readonly adminEmail = signal(sessionStorage.getItem('auto-estufa-admin') ?? '');
  protected readonly loading = signal(false);
  protected readonly toast = signal<{ message: string; type: ToastType } | null>(null);
  protected readonly clientes = signal<Cliente[]>([]);
  protected readonly estufas = signal<Estufa[]>([]);
  protected readonly agendamentos = signal<Agendamento[]>([]);
  protected readonly activeEstufas = computed(() => this.estufas().filter((estufa) => estufa.ativa));
  protected readonly todayAppointments = computed(() => {
    const today = new Date().toISOString().slice(0, 10);
    return this.agendamentos().filter((agendamento) => agendamento.dataAgendamento === today);
  });

  protected loginDraft = { email: '', senha: '' };
  protected clientSearch = '';
  protected appointmentDateFilter = '';
  protected clientModalOpen = false;
  protected appointmentModalOpen = false;
  protected editingClientId: number | null = null;
  protected editingAppointmentId: number | null = null;
  protected clientDraft: Cliente = this.emptyClient();
  protected appointmentDraft = this.emptyAppointment();

  ngOnInit(): void {
    if (this.loggedIn()) {
      this.loadData();
    }
  }

  protected signIn(): void {
    if (!this.loginDraft.email || !this.loginDraft.senha) {
      this.showToast('Informe e-mail e senha para continuar.', 'error');
      return;
    }

    this.loading.set(true);
    this.api.login(this.loginDraft).subscribe({
      next: (session) => {
        sessionStorage.setItem('auto-estufa-admin', session.email);
        this.adminEmail.set(session.email);
        this.loggedIn.set(true);
        this.loginDraft.senha = '';
        this.showToast('Login realizado. Bem-vindo ao painel.', 'success');
        this.loadData();
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.loading.set(false);
        this.showToast(this.api.mensagemDeErro(error), 'error');
      },
    });
  }

  protected signOut(): void {
    sessionStorage.removeItem('auto-estufa-admin');
    this.loggedIn.set(false);
    this.adminEmail.set('');
    this.activeView.set('dashboard');
    this.showToast('Sessao encerrada.', 'info');
  }

  protected navigate(view: View): void {
    this.activeView.set(view);
    if (view === 'clientes') {
      this.loadClientes();
    }
    if (view === 'agendamentos') {
      this.loadAgendamentos();
      this.loadClientes();
      this.loadEstufas();
    }
  }

  protected loadData(): void {
    this.loadClientes();
    this.loadEstufas();
    this.loadAgendamentos();
  }

  protected loadClientes(): void {
    this.api.listarClientes(this.clientSearch).subscribe({
      next: (clientes) => this.clientes.set(clientes),
      error: (error: unknown) => this.showToast(this.api.mensagemDeErro(error), 'error'),
    });
  }

  protected loadEstufas(): void {
    this.api.listarEstufas().subscribe({
      next: (estufas) => this.estufas.set(estufas),
      error: (error: unknown) => this.showToast(this.api.mensagemDeErro(error), 'error'),
    });
  }

  protected loadAgendamentos(): void {
    this.api.listarAgendamentos(this.appointmentDateFilter).subscribe({
      next: (agendamentos) => this.agendamentos.set(agendamentos),
      error: (error: unknown) => this.showToast(this.api.mensagemDeErro(error), 'error'),
    });
  }

  protected openClientModal(cliente?: Cliente): void {
    this.editingClientId = cliente?.id ?? null;
    this.clientDraft = cliente ? { ...cliente } : this.emptyClient();
    this.clientModalOpen = true;
  }

  protected closeClientModal(): void {
    this.clientModalOpen = false;
    this.editingClientId = null;
  }

  protected saveClient(): void {
    if (!this.clientDraft.nome || !this.clientDraft.cpf || !this.clientDraft.email) {
      this.showToast('Nome, CPF e e-mail sao obrigatorios.', 'error');
      return;
    }

    const request = this.editingClientId
      ? this.api.atualizarCliente(this.editingClientId, this.clientDraft)
      : this.api.criarCliente(this.clientDraft);

    request.subscribe({
      next: () => {
        const wasEditing = this.editingClientId !== null;
        this.closeClientModal();
        this.loadClientes();
        this.showToast(wasEditing ? 'Cliente atualizado.' : 'Cliente cadastrado.', 'success');
      },
      error: (error: unknown) => this.showToast(this.api.mensagemDeErro(error), 'error'),
    });
  }

  protected deleteClient(cliente: Cliente): void {
    if (!cliente.id || !window.confirm(`Excluir o cliente ${cliente.nome}?`)) {
      return;
    }

    this.api.excluirCliente(cliente.id).subscribe({
      next: () => {
        this.loadClientes();
        this.showToast('Cliente excluido.', 'success');
      },
      error: (error: unknown) => this.showToast(this.api.mensagemDeErro(error), 'error'),
    });
  }

  protected openAppointmentModal(agendamento?: Agendamento): void {
    this.editingAppointmentId = agendamento?.id ?? null;
    this.appointmentDraft = agendamento
      ? {
          clientId: agendamento.cliente?.id ?? null,
          estufaId: agendamento.estufa?.id ?? null,
          dataAgendamento: agendamento.dataAgendamento,
          horaInicio: agendamento.horaInicio,
          horaFim: agendamento.horaFim,
          servico: agendamento.servico,
          status: agendamento.status || 'AGENDADO',
        }
      : this.emptyAppointment();
    this.appointmentModalOpen = true;
    this.loadClientes();
    this.loadEstufas();
  }

  protected closeAppointmentModal(): void {
    this.appointmentModalOpen = false;
    this.editingAppointmentId = null;
  }

  protected saveAppointment(): void {
    const clientId = this.appointmentDraft.clientId;
    const estufaId = this.appointmentDraft.estufaId;
    if (!clientId || !estufaId || !this.appointmentDraft.dataAgendamento
      || !this.appointmentDraft.horaInicio || !this.appointmentDraft.horaFim
      || !this.appointmentDraft.servico) {
      this.showToast('Preencha cliente, estufa, data, horario e servico.', 'error');
      return;
    }

    const payload: Agendamento = {
      cliente: { id: clientId, nome: '', cpf: '', telefone: '', email: '' },
      estufa: { id: estufaId, nome: '', ativa: true },
      dataAgendamento: this.appointmentDraft.dataAgendamento,
      horaInicio: this.appointmentDraft.horaInicio,
      horaFim: this.appointmentDraft.horaFim,
      servico: this.appointmentDraft.servico,
      status: this.appointmentDraft.status || 'AGENDADO',
    };

    const request = this.editingAppointmentId
      ? this.api.atualizarAgendamento(this.editingAppointmentId, payload)
      : this.api.criarAgendamento(payload);

    request.subscribe({
      next: () => {
        const wasEditing = this.editingAppointmentId !== null;
        this.closeAppointmentModal();
        this.loadAgendamentos();
        this.showToast(wasEditing ? 'Agendamento atualizado.' : 'Agendamento criado.', 'success');
      },
      error: (error: unknown) => this.showToast(this.api.mensagemDeErro(error), 'error'),
    });
  }

  protected deleteAppointment(agendamento: Agendamento): void {
    if (!agendamento.id || !window.confirm('Excluir este agendamento?')) {
      return;
    }

    this.api.excluirAgendamento(agendamento.id).subscribe({
      next: () => {
        this.loadAgendamentos();
        this.showToast('Agendamento excluido.', 'success');
      },
      error: (error: unknown) => this.showToast(this.api.mensagemDeErro(error), 'error'),
    });
  }

  protected clienteNome(id: number | undefined): string {
    return this.clientes().find((cliente) => cliente.id === id)?.nome ?? 'Cliente removido';
  }

  protected estufaNome(id: number | undefined): string {
    return this.estufas().find((estufa) => estufa.id === id)?.nome ?? 'Estufa removida';
  }

  protected formatDate(date: string): string {
    if (!date) {
      return '-';
    }
    return new Intl.DateTimeFormat('pt-BR', { dateStyle: 'medium' }).format(new Date(`${date}T12:00:00`));
  }

  private showToast(message: string, type: ToastType): void {
    this.toast.set({ message, type });
    window.setTimeout(() => this.toast.set(null), 3600);
  }

  private emptyClient(): Cliente {
    return { nome: '', cpf: '', telefone: '', email: '' };
  }

  private emptyAppointment(): {
    clientId: number | null;
    estufaId: number | null;
    dataAgendamento: string;
    horaInicio: string;
    horaFim: string;
    servico: string;
    status: string;
  } {
    return {
      clientId: null,
      estufaId: null,
      dataAgendamento: new Date().toISOString().slice(0, 10),
      horaInicio: '08:00',
      horaFim: '09:00',
      servico: '',
      status: 'AGENDADO',
    };
  }
}
