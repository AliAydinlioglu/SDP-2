/*
  Warnings:

  - You are about to alter the column `datum` on the `meldingen` table. The data in that column could be lost. The data in that column will be cast from `DateTime(0)` to `DateTime`.

*/
-- AlterTable
ALTER TABLE `meldingen` MODIFY `datum` DATETIME NOT NULL;
