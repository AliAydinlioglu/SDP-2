-- CreateTable
CREATE TABLE `users` (
    `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
    `voornaam` VARCHAR(127) NOT NULL,
    `achternaam` VARCHAR(127) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `geboorteDatum` DATE NOT NULL,
    `gsm_nr` VARCHAR(127) NULL,
    `huis_nr` VARCHAR(10) NOT NULL,
    `straat` VARCHAR(255) NOT NULL,
    `stad` VARCHAR(255) NOT NULL,
    `postcode` VARCHAR(127) NOT NULL,
    `land` VARCHAR(127) NOT NULL,
    `hashed_password` VARCHAR(255) NOT NULL,
    `rol` ENUM('VERANTWOORDELIJKE', 'TECHNIEKER', 'ADMINISTRATOR', 'GEBRUIKER', 'MANAGER') NOT NULL DEFAULT 'GEBRUIKER',
    `actief` BOOLEAN NOT NULL DEFAULT true,

    UNIQUE INDEX `idx_klant_email_unique`(`email`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `machines` (
    `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
    `site_id` INTEGER UNSIGNED NOT NULL,
    `locatie` VARCHAR(127) NOT NULL,
    `info` VARCHAR(200) NOT NULL,
    `status` VARCHAR(127) NOT NULL,
    `prod_status` VARCHAR(127) NOT NULL,
    `uptime` INTEGER UNSIGNED NOT NULL,
    `technieker_id` INTEGER UNSIGNED NOT NULL,
    `dagenSindsOnderhoud` INTEGER UNSIGNED NOT NULL,
    `volgendOnderhoud` DATE NOT NULL,

    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `onderhouden` (
    `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
    `datum` DATE NOT NULL,
    `startTijd` TIME NOT NULL,
    `eindTijd` TIME NOT NULL,
    `technieker_id` INTEGER UNSIGNED NOT NULL,
    `reden` VARCHAR(127) NOT NULL,
    `rapport` JSON NOT NULL,
    `opmerkingen` VARCHAR(127) NOT NULL,
    `status` VARCHAR(20) NOT NULL,
    `machine_id` INTEGER UNSIGNED NOT NULL,

    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `sites` (
    `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
    `naam` VARCHAR(127) NOT NULL,
    `verantw_id` INTEGER UNSIGNED NOT NULL,

    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `meldingen` (
    `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
    `beschrijving` VARCHAR(200) NOT NULL,
    `user_id` INTEGER UNSIGNED NOT NULL,
    `status` VARCHAR(30) NOT NULL,
    `type` VARCHAR(50) NOT NULL,
    `datum` DATETIME NOT NULL,

    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- AddForeignKey
ALTER TABLE `machines` ADD CONSTRAINT `fk_machine_site` FOREIGN KEY (`site_id`) REFERENCES `sites`(`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE `machines` ADD CONSTRAINT `fk_machine_user` FOREIGN KEY (`technieker_id`) REFERENCES `users`(`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE `onderhouden` ADD CONSTRAINT `fk_onderhoud_user` FOREIGN KEY (`technieker_id`) REFERENCES `users`(`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE `onderhouden` ADD CONSTRAINT `fk_onderhoud_machine` FOREIGN KEY (`machine_id`) REFERENCES `machines`(`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE `sites` ADD CONSTRAINT `fk_site_user` FOREIGN KEY (`verantw_id`) REFERENCES `users`(`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- AddForeignKey
ALTER TABLE `meldingen` ADD CONSTRAINT `fk_melding_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;
