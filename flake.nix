{
  description = ''
    === Feel Chat Development Environment

    !!! Dev Environments
    - frontend: Creates a Flutter development Environment with an android emulator
    - backend: Creates a Java Spring Development Environment

    !!! Runnables
    - backend: runs the backend projects.
    - start-db: Creates and initialize a PostgreSQL database that store the data in a folder called .pgdata
    - stop-db: Stop the PostgreSQL database
    - reset-db: Reset (delete the .pgdata folder) and Stop the PostgreSQL database
    - emulator: Runs a Android emulator
  '';

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-25.11";
  };

  outputs = {
    self,
    nixpkgs,
  }: let
    system = "x86_64-linux";
    pkgs = nixpkgs.legacyPackages.${system};

    DB_URL = "jdbc:postgresql://localhost:5432/dev";
    DB_USER = "dbuser";
    DB_PASSWORD = "dbpass";
  in {
    # Environments
    devShells."${system}" = {
      # Backend
      backend = pkgs.mkShell {
        buildInputs = with pkgs; [
          openjdk25
          maven3
        ];

        DB_URL = "${DB_URL}";
        DB_USER = "${DB_USER}";
        DB_PASSWORD = "${DB_PASSWORD}";
        PORT = 9090;
        JWT_SECRET = 12345;
        JWT_EXPIRATION = 864000;

        JAVA_HOME = "${pkgs.openjdk25.home}";

        shellHook = '''';
      };

      # Frontend
      frontend = pkgs.mkShell {
        buildInputs = [];
        shellHook = ''
          nix develop --refresh github:K1-mikaze/Nix-Environments/main?dir=flakes/language/dart
        '';
      };
    };

    # Runnables
    apps."${system}" = {
      backend = {
        type = "app";
        program = let
          script = pkgs.writeShellScriptBin "start-backend" ''
            if [[ $(basename "$PWD") == "backend" ]]; then
              export DB_URL="${DB_URL}";
              export DB_USER="${DB_USER}";
              export DB_PASSWORD="${DB_PASSWORD}";
              export JAVA_HOME="${pkgs.openjdk25.home}";
              ./mvnw clean
              ./mvnw spring-boot:run
            else
              echo "> You're not in the required folder 'backend/' "
              exit
            fi
          '';
        in "${script}/bin/start-backend";
      };

      frontend = {
        type = "app";
        program = let
          script = pkgs.writeShellScriptBin "start-frontend" ''
            if [[ $(basename "$PWD") == "frontend" ]]; then
              ${pkgs.flutter}/bin/flutter config --android-sdk ./android/sdk/
              ${pkgs.flutter}/bin/flutter run
            else
              echo "> You're not in the required folder 'frontend/' "
              exit
            fi
          '';
        in "${script}/bin/start-frontend";
      };

      emulator = {
        type = "app";
        program = let
          script = pkgs.writeShellScriptBin "start-emulator" ''
            nix run --refresh github:K1-mikaze/Nix-Environments/main?dir=flakes/language/dart#emulator
          '';
        in "${script}/bin/start-emulator";
      };

      start-db = {
        type = "app";
        program = let
          script = pkgs.writeShellScriptBin "start-database" ''
            if [[ $(basename "$PWD") == "backend" ]]; then
              nix develop --refresh github:K1-mikaze/Nix-Environments/main?dir=flakes/database/postgresql
            else
              echo "> You're not in the required folder 'Feel-Chat/' "
              exit
            fi
          '';
        in "${script}/bin/start-database";
      };

      stop-db = {
        type = "app";
        program = let
          script = pkgs.writeShellScriptBin "stop-database" ''
            nix run github:K1-mikaze/Nix-Environments/main?dir=flakes/database/postgresql#stop
          '';
        in "${script}/bin/stop-database";
      };

      reset-db = {
        type = "app";
        program = let
          script = pkgs.writeShellScriptBin "reset-database" ''
            nix run github:K1-mikaze/Nix-Environments/main?dir=flakes/database/postgresql#reset
          '';
        in "${script}/bin/reset-database";
      };
    };
  };
}
