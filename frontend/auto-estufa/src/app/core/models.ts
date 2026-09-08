export interface AdminSession {
  email: string;
  mensagem: string;
}

export interface Cliente {
  id?: number;
  nome: string;
  cpf: string;
  telefone: string;
  email: string;
}

export interface Estufa {
  id?: number;
  nome: string;
  ativa: boolean;
}

export interface Agendamento {
  id?: number;
  cliente: Cliente;
  estufa: Estufa;
  dataAgendamento: string;
  horaInicio: string;
  horaFim: string;
  servico: string;
  status: string;
}

export interface LoginPayload {
  email: string;
  senha: string;
}
