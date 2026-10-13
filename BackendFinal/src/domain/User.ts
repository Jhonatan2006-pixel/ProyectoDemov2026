export interface User {
    id?: number;
    nombre: string;
    email: string;
    telefono?: string;
    password: string;
    rol?: string;
    created_at?: Date;
}