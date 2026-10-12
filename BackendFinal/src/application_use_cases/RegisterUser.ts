import { User } from "../domain/User";
import { UsuarioRepository } from "../infrastructure/repositories/UsuarioRepository";

export class RegisterUser {
  constructor(private usuarioRepository: UsuarioRepository) {}

  async execute(user: User) {
    return await this.usuarioRepository.create(user);
  }
}