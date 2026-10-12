import { Request, Response, NextFunction } from "express";

export const soloCensista = (
    req: Request,
    res: Response,
    next: NextFunction
): void => {

    const user = (req as any).user;

    if (!user || user.rol !== "CENSISTA") {
        res.status(403).json({
            success: false,
            message: "Acceso permitido únicamente para censistas",
            data: null
        });
        return;
    }

    next();
};


export const soloAdmin = (
    req: Request,
    res: Response,
    next: NextFunction
): void => {

    const user = (req as any).user;

    if (!user || user.rol !== "ADMIN") {
        res.status(403).json({
            success: false,
            message: "Acceso permitido únicamente para administradores",
            data: null
        });
        return;
    }

    next();
};