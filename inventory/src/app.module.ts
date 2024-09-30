import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { DatabaseService } from './api/database/database.service';

@Module({
  imports: [],
  controllers: [AppController],
  providers: [AppService, DatabaseService],
})
export class AppModule {}
