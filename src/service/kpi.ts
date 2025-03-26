import { Rol } from "@prisma/client";
import data from "../data";

const workingStatus = "Draait"; // (Draait, Gestopt)
const productieGezondStatus = "gezond"; // (Gezond, Nood aan onderhoud, falend)
const productieFalendStatus = "falend"; // (Gezond, Nood aan onderhoud, falend)
const productieOnderhoudStatus = "nood aan onderhoud"; // (Gezond, Nood aan onderhoud, falend)

// Get machine-related KPIs.
const getMachineKPIs = async () => {
    // Count machines that are working.
    const workingMachinesCount = await data.prisma.machine.count({
        where: { status: workingStatus },
    });

    // Total machines.
    const totalMachinesCount = await data.prisma.machine.count();

    // Count machines with a problem.
    const machinesInProblemCount = await data.prisma.machine.count({
        where: { prod_status: productieFalendStatus },
    });

    const machinesInMaintenanceCount = await data.prisma.machine.count({
        where: { prod_status: productieOnderhoudStatus },
    });

    const machinesInGoodConditionCount = await data.prisma.machine.count({
        where: { prod_status: productieGezondStatus },
    });

    return {
        totalMachines: totalMachinesCount,
        workingMachines: workingMachinesCount,

        machinesInGoodCondition: machinesInGoodConditionCount,
        machinesFailing: machinesInProblemCount,
        machinesInMaintenance: machinesInMaintenanceCount,
    };
};

// Get technician-related KPIs.
const getTechnicianKPIs = async () => {
    // Total technicians (users with role TECHNIEKER).
    const totalTechnicians = await data.prisma.user.count({
        where: { rol: Rol.TECHNIEKER },
    });

    // Count unique technicians assigned to machines.
    const assignedTechs = await data.prisma.machine.findMany({
        distinct: ["technieker_id"],
        select: { technieker_id: true },
    });
    const assignedTechnicians = assignedTechs.length;

    return {
        totalTechnicians,
        assignedTechnicians,
    };
};

export const getKPIs = async () => {
    const machineKPIs = await getMachineKPIs();
    const technicianKPIs = await getTechnicianKPIs();

    return {
        ...machineKPIs,
        ...technicianKPIs,
    };
};

export default { getKPIs };
