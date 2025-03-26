import { PrismaClient, Action } from "@prisma/client";

const prisma = new PrismaClient();

export interface AuditLogData {
    userId: number;
    machineId?: number;
    details?: any;
}

export async function logCreate(auditData: AuditLogData) {
    try {
        const auditLog = await prisma.auditLog.create({
            data: {
                userId: auditData.userId,
                action: Action.CREATE,
                machineId: auditData.machineId,
                details: auditData.details,
            },
        });
        return auditLog;
    } catch (error) {
        console.error("Error creating audit log:", error);
    }
}
